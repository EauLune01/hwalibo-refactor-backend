package hwalibo.refactor.review.dto.response;

import hwalibo.refactor.review.dto.ReviewImageInfo;
import hwalibo.refactor.review.dto.result.ReviewCreateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateResponse {
    private Long reviewId;
    private List<ReviewImageInfo> images;

    public static ReviewCreateResponse from(ReviewCreateResult result) {
        return ReviewCreateResponse.builder()
                .reviewId(result.getReviewId())
                .images(result.getImages())
                .build();
    }
}
