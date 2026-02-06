package hwalibo.refactor.review.dto.result;

import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.domain.ReviewImage;
import hwalibo.refactor.review.domain.Tag;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReviewResult {
    private Long reviewId;
    private String content;
    private Double rating;
    private Integer likeCount;
    private boolean isLiked;
    private List<String> imageUrls;
    private List<Tag> tags;
    private boolean isDisabledAccess;
    private String writerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ReviewResult(Review review, boolean isLiked) {
        this.reviewId = review.getId();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.likeCount = review.getLikeCount();
        this.isLiked = isLiked;
        this.imageUrls = review.getReviewImages().stream()
                .map(ReviewImage::getUrl)
                .toList();
        this.tags = review.getTags();
        this.isDisabledAccess = review.isDisabledAccess();
        this.writerName = review.getUser().getName();
        this.createdAt = review.getCreatedAt();
        this.updatedAt=review.getUpdatedAt();
    }

    public static ReviewResult of(Review review, boolean isLiked) {
        return new ReviewResult(review, isLiked);
    }
}
