package hwalibo.refactor.review.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class PhotoReviewResult {
    private final Long reviewId;
    private final Long photoId;
    private final String photoUrl;

    @QueryProjection
    public PhotoReviewResult(Long reviewId, Long photoId, String photoUrl) {
        this.reviewId = reviewId;
        this.photoId = photoId;
        this.photoUrl = photoUrl;
    }
}
