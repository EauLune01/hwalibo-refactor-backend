package hwalibo.refactor.global.service;

import hwalibo.refactor.global.exception.image.ImageNotFoundException;
import hwalibo.refactor.review.domain.ReviewImage;
import hwalibo.refactor.review.repository.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImageValidationService {

    private final ChatClient.Builder chatClientBuilder;
    private final ReviewImageRepository reviewImageRepository;

    @Async("imageTaskExecutor")
    public void validateReviewImage(Long reviewImageId) {
        ReviewImage reviewImage = reviewImageRepository.findById(reviewImageId)
                .orElseThrow(() -> new ImageNotFoundException("이미지를 찾을 수 없습니다."));

        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();

        log.info("📸 [리뷰 이미지 {}] AI 검증 시작 (gpt-4o-mini)", reviewImageId);

        try {
            String result = chatClient.prompt()
                    .user(u -> {
                        try {
                            u.text("이 사진이 화장실 내부, 입구, 세면대 관련 이미지인지 확인해줘. " +
                                            "조건에 맞으면 'OK', 아니면 'REJECT'라고 짧게 답해.")
                                    .media(MimeTypeUtils.IMAGE_JPEG, new URL(reviewImage.getUrl()));
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .call()
                    .content();

            if ("OK".equalsIgnoreCase(result.trim())) {
                reviewImage.approve();
                log.info("✅ [리뷰 이미지 {}] 검증 승인", reviewImageId);
            } else {
                reviewImage.reject();
                log.warn("🚨 [리뷰 이미지 {}] 검증 거부 - 부적절한 이미지", reviewImageId);
            }
        } catch (Exception e) {
            log.error("❌ [리뷰 이미지 {}] AI 검증 중 오류: {}", reviewImageId, e.getMessage());
        }
    }
}
