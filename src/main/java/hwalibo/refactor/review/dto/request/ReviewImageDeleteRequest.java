package hwalibo.refactor.review.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImageDeleteRequest {

    @NotNull(message = "삭제할 이미지 ID 목록은 필수입니다.")
    private List<Long> deleteImageIds;
}
