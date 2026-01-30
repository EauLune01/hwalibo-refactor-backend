package hwalibo.refactor.review.service;

import hwalibo.refactor.global.exception.review.ReviewNotFoundException;
import hwalibo.refactor.global.exception.toilet.ToiletNotFoundException;
import hwalibo.refactor.global.exception.user.UserNotFoundException;
import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.dto.command.ReviewCreateCommand;
import hwalibo.refactor.review.dto.command.ReviewUpdateCommand;
import hwalibo.refactor.review.dto.result.ReviewCreateResult;
import hwalibo.refactor.review.repository.ReviewRepository;
import hwalibo.refactor.toilet.domain.Toilet;
import hwalibo.refactor.toilet.respository.ToiletRepository;
import hwalibo.refactor.toilet.service.ToiletCacheService;
import hwalibo.refactor.user.domain.User;
import hwalibo.refactor.user.repository.UserRepository;
import hwalibo.refactor.user.service.UserCacheService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandService {

    private final ReviewRepository reviewRepository;
    private final ToiletRepository toiletRepository;
    private final UserRepository userRepository;

    private final ReviewImageCommandService reviewImageCommandService;
    private final ToiletCacheService toiletCacheService;
    private final UserCacheService userCacheService;

    public Long createReview(ReviewCreateCommand command, List<MultipartFile> images) {
        Review savedReview = saveReview(command);

        if (images != null && !images.isEmpty()) {
            reviewImageCommandService.uploadAndSaveAll(images, savedReview);
        }

        evictAllCaches(command.getUserId(), command.getToiletId());

        return savedReview.getId();
    }

    public void updateReview(Long userId, Long reviewId, ReviewUpdateCommand command) {
        Review review = validateReviewOwner(userId, reviewId);

        updateReviewDomain(review, command);

        evictAllCaches(userId, review.getToilet().getId());
    }

    public void deleteReview(Long userId, Long reviewId) {
        Review review = validateReviewOwnerWithImages(userId, reviewId);

        reviewImageCommandService.deleteAllByReview(review);
        review.getToilet().removeReviewStats(review.getRating());
        review.getUser().removeReview();

        reviewRepository.delete(review);
        evictAllCaches(userId, review.getToilet().getId());
    }

    /******************** Helper Method ********************/
    private Review saveReview(ReviewCreateCommand command) {
        Toilet toilet = toiletRepository.findById(command.getToiletId())
                .orElseThrow(() -> new EntityNotFoundException("해당 화장실을 찾을 수 없습니다."));
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Review review = Review.create(
                user,
                toilet,
                command.getContent(),
                command.getRating(),
                command.isDisabledAccess(),
                command.getTags()
        );

        return reviewRepository.save(review);
    }

    private Review validateReviewOwner(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰가 존재하지 않습니다."));

        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("본인이 작성한 리뷰만 수정/삭제할 수 있습니다.");
        }
        return review;
    }

    private Review validateReviewOwnerWithImages(Long userId, Long reviewId) {
        Review review = reviewRepository.findReviewWithImages(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰가 존재하지 않습니다."));

        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("본인이 작성한 리뷰만 수정/삭제할 수 있습니다.");
        }
        return review;
    }

    private void updateReviewDomain(Review review, ReviewUpdateCommand command) {
        Toilet toilet = review.getToilet();
        double oldRating = review.getRating();

        toilet.updateReviewStats(oldRating, command.getRating(), false);

        review.updateReview(
                command.getContent(),
                command.getRating(),
                command.isDisabledAccess(),
                command.getTags()
        );
    }

    private void evictAllCaches(Long userId, Long toiletId) {
        userCacheService.evictUserRate(userId);
        toiletCacheService.evictToiletCache(toiletId);
    }
}