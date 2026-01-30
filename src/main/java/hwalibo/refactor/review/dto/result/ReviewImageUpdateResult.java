package hwalibo.refactor.review.dto.result;

import hwalibo.refactor.review.domain.ReviewImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewImageUpdateResult {
    private Long imageId;
    private String imageUrl;

    public static ReviewImageUpdateResult from(ReviewImage image) {
        return ReviewImageUpdateResult.builder()
                .imageId(image.getId())
                .imageUrl(image.getUrl())
                .build();
    }
}
