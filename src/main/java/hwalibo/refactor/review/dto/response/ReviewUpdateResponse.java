package hwalibo.refactor.review.dto.response;

import hwalibo.refactor.review.dto.result.ReviewUpdateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewUpdateResponse {
    private Long reviewId;

    public static ReviewUpdateResponse from(ReviewUpdateResult result) {
        return ReviewUpdateResponse.builder()
                .reviewId(result.getReviewId())
                .build();
    }
}