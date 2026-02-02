package hwalibo.refactor.review.dto.response;

import hwalibo.refactor.review.dto.result.PhotoReviewResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PhotoReviewResponse {
    private Long reviewId;
    private Long photoId;
    private String photoUrl;

    public static PhotoReviewResponse from(PhotoReviewResult result) {
        return PhotoReviewResponse.builder()
                .reviewId(result.getReviewId())
                .photoId(result.getPhotoId())
                .photoUrl(result.getPhotoUrl())
                .build();
    }
}
