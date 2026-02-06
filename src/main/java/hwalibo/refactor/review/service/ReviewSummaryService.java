package hwalibo.refactor.review.service;

import hwalibo.refactor.global.exception.review.ReviewNotFoundException;
import hwalibo.refactor.global.exception.review.SummaryGenerationException;
import hwalibo.refactor.global.utils.OpenAISummaryProvider;
import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.dto.result.ReviewSummaryResult;
import hwalibo.refactor.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewSummaryService {

    private final ReviewRepository reviewRepository;
    private final OpenAISummaryProvider openAISummaryProvider;

    @Cacheable(value = "review-summaries", key = "#toiletId")
    public ReviewSummaryResult summarizeByToiletId(Long toiletId,Pageable pageable) {
        List<Review> reviews = getLatestReviewsOrThrow(toiletId, pageable);
        String combinedText = combineReviewContentsOrThrow(reviews);
        String summary = requestSummaryWithExceptionHandling(combinedText);
        return new ReviewSummaryResult(summary);
    }

    /******************** Helper Method ********************/
    private List<Review> getLatestReviewsOrThrow(Long toiletId,Pageable pageable) {
        List<Review> reviews = reviewRepository.findByToiletIdOrderByCreatedAtDesc(toiletId, pageable);
        if (reviews.isEmpty()) {
            throw new ReviewNotFoundException("해당 화장실에 리뷰가 없습니다.");
        }
        return reviews;
    }

    private String combineReviewContentsOrThrow(List<Review> reviews) {
        String combined = reviews.stream()
                .map(Review::getContent)
                .filter(content -> content != null && !content.isBlank())
                .collect(Collectors.joining("\n"));

        if (combined.isBlank()) {
            throw new ReviewNotFoundException("요약할 리뷰 내용이 비어있습니다.");
        }
        return combined;
    }

    private String requestSummaryWithExceptionHandling(String combinedText) {
        try {
            return openAISummaryProvider.getSummaryFromOpenAI(combinedText);
        } catch (Exception e) {
            throw new SummaryGenerationException("리뷰 요약 생성 중 오류가 발생했습니다.");
        }
    }
}