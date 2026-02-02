package hwalibo.refactor.review.dto.result;

import hwalibo.refactor.review.dto.ReviewImageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReviewCreateResult {
    private Long reviewId;
    private List<ReviewImageInfo> images;

    public static ReviewCreateResult of(Long reviewId, List<ReviewImageInfo> images) {
        return ReviewCreateResult.builder()
                .reviewId(reviewId)
                .images(images != null ? images : List.of())
                .build();
    }
}
