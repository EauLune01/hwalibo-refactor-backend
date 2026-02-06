package hwalibo.refactor.reviewLike.service;

import hwalibo.refactor.global.exception.review.ReviewNotFoundException;
import hwalibo.refactor.global.exception.reviewLike.AlreadyLikedException;
import hwalibo.refactor.global.exception.reviewLike.NotLikedException;
import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.repository.ReviewRepository;
import hwalibo.refactor.reviewLike.domain.ReviewLike;
import hwalibo.refactor.reviewLike.dto.command.ReviewLikeAddCommand;
import hwalibo.refactor.reviewLike.dto.command.ReviewLikeRemoveCommand;
import hwalibo.refactor.reviewLike.repository.ReviewLikeRepository;
import hwalibo.refactor.toilet.service.ToiletCacheService;
import hwalibo.refactor.user.domain.User;
import hwalibo.refactor.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewLikeCommandService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final ToiletCacheService toiletCacheService;

    /**
     * 리뷰 좋아요 추가
     */
    public void addLike(ReviewLikeAddCommand command) {
        Review review = reviewRepository.findWithToiletById(command.getReviewId())
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
        validateLikeRequest(review, command);
        saveLikeAndEvictCache(command.getUserId(), review);
    }

    /**
     * 리뷰 좋아요 취소
     */
    public void removeLike(ReviewLikeRemoveCommand command) {
        Review review = reviewRepository.findWithToiletById(command.getReviewId())
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
        ReviewLike reviewLike = validateUnlikeRequest(review, command);
        deleteLikeAndEvictCache(review, reviewLike);
    }

    /******************** Helper Method ********************/


    private void validateLikeRequest(Review review, ReviewLikeAddCommand command) {
        if (!review.getToilet().getId().equals(command.getToiletId())) {
            throw new ReviewNotFoundException("해당 화장실의 리뷰가 아닙니다.");
        }
        if (reviewLikeRepository.existsByReviewIdAndUserId(command.getReviewId(), command.getUserId())) {
            throw new AlreadyLikedException("이미 좋아요를 누른 리뷰입니다.");
        }
    }

    private void saveLikeAndEvictCache(Long userId, Review review) {
        User user = userRepository.getReferenceById(userId);
        reviewLikeRepository.save(ReviewLike.create(user, review));
        toiletCacheService.evictToiletCache(review.getToilet().getId());
    }

    private ReviewLike validateUnlikeRequest(Review review, ReviewLikeRemoveCommand command) {
        if (!review.getToilet().getId().equals(command.getToiletId())) {
            throw new ReviewNotFoundException("해당 화장실의 리뷰가 아닙니다.");
        }
        return reviewLikeRepository.findByReviewIdAndUserId(command.getReviewId(), command.getUserId())
                .orElseThrow(() -> new NotLikedException("좋아요를 누르지 않은 리뷰입니다."));
    }

    private void deleteLikeAndEvictCache(Review review, ReviewLike reviewLike) {
        reviewLikeRepository.delete(reviewLike);
        review.removeLike();
        toiletCacheService.evictToiletCache(review.getToilet().getId());
    }
}
