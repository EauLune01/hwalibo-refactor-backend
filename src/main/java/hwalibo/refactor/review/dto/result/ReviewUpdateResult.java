package hwalibo.refactor.review.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewUpdateResult {
    private Long reviewId;

    public static ReviewUpdateResult from(Long reviewId) {
        return new ReviewUpdateResult(reviewId);
    }
}
