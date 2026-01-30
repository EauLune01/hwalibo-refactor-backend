package hwalibo.refactor.review.dto;

import hwalibo.refactor.review.domain.ReviewImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReviewImageInfo {
    private Long imageId;
    private String imageUrl;

    public static ReviewImageInfo from(ReviewImage entity) {
        return ReviewImageInfo.builder()
                .imageId(entity.getId())
                .imageUrl(entity.getUrl())
                .build();
    }
}
