package hwalibo.refactor.review.service;

import hwalibo.refactor.global.exception.review.ReviewNotFoundException;
import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.dto.ReviewImageInfo;
import hwalibo.refactor.review.dto.result.ReviewImageUpdateResult;
import hwalibo.refactor.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewImageQueryService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<ReviewImageUpdateResult> getReviewImageUpdateResults(Long reviewId) {
        Review review = reviewRepository.findReviewWithImages(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        return review.getReviewImages().stream()
                .map(ReviewImageUpdateResult::from)
                .toList();
    }
}
