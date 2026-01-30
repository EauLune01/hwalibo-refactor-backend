package hwalibo.refactor.user.dto.result;

import hwalibo.refactor.review.domain.Review;
import hwalibo.refactor.review.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserReviewResult {
    private Long id;
    private String toiletName;
    private String gender;
    private String line;
    private String content;
    private Double rating;
    private List<ImageInfo> photos;
    private List<Tag> tags;
    private boolean isDisabledAccess;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @AllArgsConstructor
    public static class ImageInfo {
        private Long id;
        private String url;
    }

    public static UserReviewResult from(Review review) {
        return UserReviewResult.builder()
                .id(review.getId())
                .toiletName(review.getToilet().getName())
                .gender(review.getToilet().getGender().name())
                .line(String.valueOf(review.getToilet().getLine()))
                .content(review.getContent())
                .rating(review.getRating())
                .photos(review.getReviewImages().stream()
                        .map(img -> new ImageInfo(img.getId(), img.getUrl()))
                        .toList())
                .tags(review.getTags())
                .isDisabledAccess(review.isDisabledAccess())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
