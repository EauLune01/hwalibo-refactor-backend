package hwalibo.refactor.review.dto.response;

import hwalibo.refactor.review.dto.result.PhotoDetailResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDetailResponse {

    private String photoUrl;
    private ReviewDetail review;

    public static PhotoDetailResponse from(PhotoDetailResult result) {
        return PhotoDetailResponse.builder()
                .photoUrl(result.getUrl())
                .review(ReviewDetail.from(result))
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewDetail {
        private Long reviewId;
        private Long userId;
        private String userName;
        private Double star;
        private String content;
        private List<String> tag;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedAt;
        private Integer likeCount;
        private boolean isDisabledAccess;

        public static ReviewDetail from(PhotoDetailResult result) {
            return ReviewDetail.builder()
                    .reviewId(result.getReviewId())
                    .userId(result.getUserId())
                    .userName(result.getName())
                    .star(result.getRating())
                    .content(result.getContent())
                    .tag(result.getTags())
                    .createdAt(result.getCreatedAt())
                    .updatedAt(result.getUpdatedAt())
                    .likeCount(result.getLikeCount())
                    .isDisabledAccess(result.isDisabledAccess())
                    .build();
        }
    }
}
