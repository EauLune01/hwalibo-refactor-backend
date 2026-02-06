package hwalibo.refactor.review.service;

import hwalibo.refactor.global.exception.review.ReviewNotFoundException;
import hwalibo.refactor.global.service.ImageValidationService;
import hwalibo.refactor.global.service.S3Service;
import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.domain.ReviewImage;
import hwalibo.refactor.review.dto.command.ReviewImageUpdateCommand;
import hwalibo.refactor.review.repository.ReviewImageRepository;
import hwalibo.refactor.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewImageCommandService {

    private final S3Service s3Service;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ImageValidationService imageValidationService;

    public void uploadAndSaveAll(List<MultipartFile> files, Review review) {
        List<String> imageUrls = s3Service.uploadAll(files, "reviews");
        if (imageUrls.isEmpty()) {return;}
        processAndValidateImages(review, imageUrls);
    }

    public void updateImages(ReviewImageUpdateCommand command) {
        Review review = reviewRepository.findById(command.getReviewId())
                .orElseThrow(() -> new ReviewNotFoundException("리뷰가 존재하지 않습니다."));
        validateReviewOwner(review, command.getUserId());
        processImageDeletion(review, command.getDeleteImageIds());
        processImageAddition(review, command.getNewPhotos());
    }

    public void deleteAllByReview(Review review) {
        List<ReviewImage> images = review.getReviewImages();
        images.forEach(image -> s3Service.delete(image.getUrl()));
        review.getReviewImages().clear();
    }

    /******************** Helper Method ********************/

    private void processAndValidateImages(Review review, List<String> imageUrls) {
        List<ReviewImage> reviewImages = imageUrls.stream()
                .map(url -> {
                    ReviewImage img = ReviewImage.create(review, url, 0);
                    review.addReviewImage(img);
                    return img;
                })
                .toList();
        reviewImageRepository.saveAll(reviewImages);
        reviewImages.forEach(img ->
                imageValidationService.validateReviewImage(img.getId())
        );
    }

    private void validateReviewOwner(Review review, Long userId) {
        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }
    }

    private void processImageDeletion(Review review, List<Long> deleteImageIds) {
        if (deleteImageIds == null || deleteImageIds.isEmpty()) return;

        List<String> deleteUrls = review.getReviewImages().stream()
                .filter(img -> deleteImageIds.contains(img.getId()))
                .map(ReviewImage::getUrl)
                .toList();

        deleteUrls.forEach(s3Service::delete);
        deleteImageIds.forEach(review::removeReviewImage);
    }

    private void processImageAddition(Review review, List<MultipartFile> newFiles) {
        if (newFiles == null || newFiles.isEmpty()) return;

        List<String> newUrls = s3Service.uploadAll(newFiles, "reviews");

        List<ReviewImage> newEntities = newUrls.stream()
                .map(url -> {
                    ReviewImage newImg = ReviewImage.create(review, url, 0);
                    review.addReviewImage(newImg);
                    return newImg;
                }).toList();

        List<ReviewImage> savedEntities = reviewImageRepository.saveAll(newEntities);

        savedEntities.forEach(img -> imageValidationService.validateReviewImage(img.getId()));
    }
}