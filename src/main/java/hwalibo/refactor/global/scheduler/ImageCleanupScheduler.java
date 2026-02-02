package hwalibo.refactor.global.scheduler;

import hwalibo.refactor.global.service.S3Service;
import hwalibo.refactor.review.domain.ReviewImage;
import hwalibo.refactor.review.domain.ValidationStatus;
import hwalibo.refactor.review.repository.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageCleanupScheduler {

    private final ReviewImageRepository reviewImageRepository;
    private final S3Service s3Service;

    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void cleanupRejectedImages() {
        log.info("🧹 [새벽 청소] REJECTED 이미지 정리를 시작합니다.");

        LocalDateTime threshold = LocalDateTime.now().minusHours(1);

        List<ReviewImage> rejectedImages = reviewImageRepository.findAllByStatusAndUpdatedAtBefore(
                ValidationStatus.REJECTED, threshold);

        if (rejectedImages.isEmpty()) {
            log.info("✅ 정리할 이미지가 없습니다.");
            return;
        }

        for (ReviewImage image : rejectedImages) {
            try {
                s3Service.delete(image.getUrl());
                reviewImageRepository.delete(image);
                log.info("🗑️ 삭제 완료: ID {}, URL {}", image.getId(), image.getUrl());
            } catch (Exception e) {
                log.error("❌ 이미지 삭제 중 오류 발생 (ID: {}): {}", image.getId(), e.getMessage());
            }
        }

        log.info("✅ 총 {}개의 부적절한 이미지를 청소했습니다.", rejectedImages.size());
    }
}
