package hwalibo.refactor.review.dto.response;

import hwalibo.refactor.review.dto.result.ReviewImageUpdateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReviewImageUpdateResponse {
    private Long reviewId;
    private List<ReviewImageUpdateResult> updatedPhotos;

    public static ReviewImageUpdateResponse of(Long reviewId, List<ReviewImageUpdateResult> results) {
        return ReviewImageUpdateResponse.builder()
                .reviewId(reviewId)
                .updatedPhotos(results)
                .build();
    }
}
