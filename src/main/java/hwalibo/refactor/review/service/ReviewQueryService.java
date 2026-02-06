package hwalibo.refactor.review.service;

import hwalibo.refactor.global.exception.review.ReviewNotFoundException;
import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.dto.ReviewImageInfo;
import hwalibo.refactor.review.dto.query.ReviewSearchCondition;
import hwalibo.refactor.review.dto.result.ReviewCreateResult;
import hwalibo.refactor.review.dto.result.ReviewResult;
import hwalibo.refactor.review.dto.result.ReviewUpdateResult;
import hwalibo.refactor.review.repository.ReviewRepository;
import hwalibo.refactor.reviewLike.repository.ReviewLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public Slice<ReviewResult> getReviewList(Long userId, Long toiletId, ReviewSearchCondition condition, Pageable pageable) {
        List<Long> likedReviewIds = reviewLikeRepository.findReviewIdsByUserId(userId);
        Slice<Review> reviews = reviewRepository.findReviewList(toiletId, condition, likedReviewIds, pageable);
        return reviews.map(review ->
                ReviewResult.of(review, likedReviewIds.contains(review.getId()))
        );
    }

    public ReviewCreateResult getReviewCreateResult(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰 생성 확인 중 오류가 발생했습니다."));

        List<ReviewImageInfo> imageInfos = review.getReviewImages().stream()
                .map(ReviewImageInfo::from)
                .toList();

        return ReviewCreateResult.of(review.getId(), imageInfos);
    }

    public ReviewUpdateResult getReviewUpdateResult(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("수정된 리뷰를 찾을 수 없습니다."));

        return ReviewUpdateResult.from(review.getId());
    }
}
