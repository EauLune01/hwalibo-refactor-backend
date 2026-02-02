package hwalibo.refactor.review.service;

import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.global.exception.image.ImageNotFoundException;
import hwalibo.refactor.global.exception.review.ReviewNotFoundException;
import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.domain.ReviewImage;
import hwalibo.refactor.review.dto.result.PhotoDetailResult;
import hwalibo.refactor.review.dto.result.PhotoReviewResult;
import hwalibo.refactor.review.dto.result.ReviewImageUpdateResult;
import hwalibo.refactor.review.repository.ReviewImageRepository;
import hwalibo.refactor.review.repository.ReviewRepository;
import hwalibo.refactor.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewImageQueryService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    public List<ReviewImageUpdateResult> getReviewImageUpdateResults(Long reviewId) {
        Review review = reviewRepository.findReviewWithImages(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        return review.getReviewImages().stream()
                .map(ReviewImageUpdateResult::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<PhotoReviewResult> getPhotoReviews(Long toiletId, Gender gender, Pageable pageable) {
        return reviewImageRepository.findSliceByToiletId(toiletId, gender, pageable);
    }

    public PhotoDetailResult getPhotoDetail(Long photoId, Gender gender) {
        ReviewImage image = reviewImageRepository.findPhotoDetail(photoId, gender)
                .orElseThrow(() -> new ImageNotFoundException("사진을 찾을 수 없거나 접근 권한이 없습니다."));

        return PhotoDetailResult.from(image);
    }
}
