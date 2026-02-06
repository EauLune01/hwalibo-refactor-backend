package hwalibo.refactor.review.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import hwalibo.refactor.review.dto.result.ReviewResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewResponse {
    private Long reviewId;
    private String content;
    private Double rating;
    private Integer likeCount;
    private boolean isLiked;
    private List<String> imageUrls;
    private List<String> tags;
    private boolean isDisabledAccess;
    private String writerName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static ReviewResponse from(ReviewResult result) {
        return ReviewResponse.builder()
                .reviewId(result.getReviewId())
                .content(result.getContent())
                .rating(result.getRating())
                .likeCount(result.getLikeCount())
                .isLiked(result.isLiked())
                .imageUrls(result.getImageUrls())
                .tags(result.getTags().stream().map(Enum::name).toList())
                .isDisabledAccess(result.isDisabledAccess())
                .writerName(result.getWriterName())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();
    }
}
