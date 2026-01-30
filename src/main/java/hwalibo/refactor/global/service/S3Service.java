package hwalibo.refactor.global.service;

import hwalibo.refactor.global.exception.image.ImageCountInvalidException;
import hwalibo.refactor.global.exception.image.ImageNotFoundException;
import hwalibo.refactor.global.exception.image.InvalidImageException;
import hwalibo.refactor.global.utils.S3KeyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 다중 파일 업로드 (최소 0장, 최대 2장 규칙 적용)
     */
    public List<String> uploadAll(List<MultipartFile> files, String dirName) {
        int fileCount = (files == null) ? 0 : (int) files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .count();

        if (fileCount == 0) {
            return List.of();
        }

        if (fileCount > 2) {
            throw new ImageCountInvalidException("사진은 최대 2장까지만 등록 가능합니다.");
        }

        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> upload(file, dirName))
                .toList();
    }

    /**
     * 단일 파일 업로드
     */
    public String upload(MultipartFile file, String dirName) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("업로드할 이미지 파일이 유효하지 않습니다.");
        }

        String key = dirName + "/" + UUID.randomUUID() + getExt(file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("S3 업로드 성공: {}", key);
            return getPublicUrl(key);

        } catch (IOException e) {
            log.error("S3 파일 읽기 실패: {}", file.getOriginalFilename());
            throw new InvalidImageException("이미지 파일을 읽는 중 오류가 발생했습니다.");
        } catch (S3Exception e) {
            log.error("AWS S3 서비스 에러: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 파일 삭제 (S3KeyUtils 활용)
     */
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        try {
            String key = S3KeyUtils.toKey(bucket, fileUrl);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("S3 삭제 완료: {}", key);
        } catch (NoSuchKeyException e) {
            throw new ImageNotFoundException("삭제하려는 이미지가 S3에 존재하지 않습니다: " + fileUrl);
        } catch (Exception e) {
            log.error("S3 삭제 실패: {}", fileUrl, e);
        }
    }

    private String getPublicUrl(String key) {
        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucket, key);
    }

    private String getExt(String original) {
        if (original == null) return "";
        int idx = original.lastIndexOf('.');
        return (idx >= 0) ? original.substring(idx) : "";
    }
}