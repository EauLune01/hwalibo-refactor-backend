package hwalibo.refactor.review.dto.command;

import hwalibo.refactor.review.domain.Tag;
import hwalibo.refactor.review.dto.request.ReviewCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReviewCreateCommand {
    private Long toiletId;
    private Long userId;
    private String content;
    private Double rating;
    private boolean isDisabledAccess;
    private List<Tag> tags;

    public static ReviewCreateCommand of(ReviewCreateRequest request, Long toiletId, Long userId) {
        return ReviewCreateCommand.builder()
                .toiletId(toiletId)
                .userId(userId)
                .content(request.getDesc())
                .rating(request.getStar())
                .isDisabledAccess(request.getIsDisabledAccess())
                .tags(request.getTag() != null ?
                        request.getTag().stream().map(Tag::valueOf).toList() :
                        new ArrayList<>())
                .build();
    }
}
