package hwalibo.refactor.review.dto.request;

import hwalibo.refactor.review.domain.Tag;
import hwalibo.refactor.review.dto.command.ReviewCreateCommand;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequest {

    @NotBlank(message = "리뷰 내용은 필수 입력값입니다.")
    private String content;

    @NotNull(message = "별점은 필수 입력값입니다.")
    @DecimalMin(value = "0.5", message = "별점은 0.5 이상이어야 합니다.")
    @DecimalMax(value = "5.0", message = "별점은 5.0 이하이어야 합니다.")
    private Double star;

    private List<Tag> tags;

    @NotNull(message = "장애인 화장실 여부를 선택해주세요.")
    private Boolean isDisabledAccess;

    public static ReviewCreateCommand of(ReviewCreateRequest request, Long toiletId, Long userId) {
        return ReviewCreateCommand.builder()
                .toiletId(toiletId)
                .userId(userId)
                .content(request.getContent())
                .rating(request.getStar())
                .isDisabledAccess(request.getIsDisabledAccess())
                .tags(request.getTags() != null ? request.getTags() : new ArrayList<>())
                .build();
    }
}
