package hwalibo.refactor.review.dto.request;

import hwalibo.refactor.review.domain.Tag;
import hwalibo.refactor.review.dto.command.ReviewUpdateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdateRequest {

    @NotNull(message = "별점은 필수입니다.")
    private Double star;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    private List<Tag> tags;

    @NotNull(message = "장애인 화장실 여부는 필수입니다.")
    private boolean isDisabledAccess;

    public ReviewUpdateCommand toCommand() {
        return ReviewUpdateCommand.builder()
                .rating(this.star)
                .content(this.content)
                .tags(this.tags)
                .isDisabledAccess(this.isDisabledAccess)
                .build();
    }
}
