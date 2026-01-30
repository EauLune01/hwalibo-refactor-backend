package hwalibo.refactor.review.dto.command;

import hwalibo.refactor.review.dto.request.ReviewImageDeleteRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReviewImageUpdateCommand {
    private Long userId;
    private Long reviewId;
    private List<Long> deleteImageIds;
    private List<MultipartFile> newPhotos;

    public static ReviewImageUpdateCommand of(Long userId, Long reviewId,
                                              ReviewImageDeleteRequest request,
                                              List<MultipartFile> photos) {
        return ReviewImageUpdateCommand.builder()
                .userId(userId)
                .reviewId(reviewId)
                .deleteImageIds(request != null && request.getDeleteImageIds() != null
                        ? request.getDeleteImageIds() : new ArrayList<>())
                .newPhotos(photos != null ? photos : new ArrayList<>())
                .build();
    }
}
