package hwalibo.refactor.review.dto.result;

import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.domain.ReviewImage;
import hwalibo.refactor.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PhotoDetailResult {
    private String url;
    private Long reviewId;
    private String content;
    private Double rating;
    private Integer likeCount;
    private boolean isDisabledAccess;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String name;
    private List<String> tags;

    public static PhotoDetailResult from(ReviewImage image) {
        Review review = image.getReview();
        User user = review.getUser();

        return PhotoDetailResult.builder()
                .url(image.getUrl())
                .reviewId(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .likeCount(review.getLikeCount())
                .isDisabledAccess(review.isDisabledAccess())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .userId(user.getId())
                .name(user.getName())
                .tags(review.getTags().stream().map(Enum::name).toList())
                .build();
    }
}
