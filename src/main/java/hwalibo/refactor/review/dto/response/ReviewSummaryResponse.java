package hwalibo.refactor.review.dto.response;

import hwalibo.refactor.review.dto.result.ReviewSummaryResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewSummaryResponse {
    private String summary;

    public static ReviewSummaryResponse from(ReviewSummaryResult result) {
        return new ReviewSummaryResponse(result.getSummary());
    }
}
