package hwalibo.refactor.review.dto.result;

import hwalibo.refactor.review.domain.ReviewImage;
import hwalibo.refactor.review.dto.ReviewImageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReviewImageUpdateResult {

    private ReviewImageInfo image;

    public static ReviewImageUpdateResult from(ReviewImage image) {
        return ReviewImageUpdateResult.builder()
                .image(ReviewImageInfo.from(image))
                .build();
    }
}

