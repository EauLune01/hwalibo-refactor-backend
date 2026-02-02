package hwalibo.refactor.review.dto.command;

import hwalibo.refactor.review.domain.Tag;
import hwalibo.refactor.review.dto.request.ReviewUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReviewUpdateCommand {
    private Double rating;
    private String content;
    private List<Tag> tags;
    private boolean isDisabledAccess;

    public static ReviewUpdateCommand of(ReviewUpdateRequest request) {
        return ReviewUpdateCommand.builder()
                .rating(request.getStar())
                .content(request.getContent())
                .isDisabledAccess(request.isDisabledAccess())
                .tags(request.getTags() != null ?
                        request.getTags() : new ArrayList<>())
                .build();
    }
}