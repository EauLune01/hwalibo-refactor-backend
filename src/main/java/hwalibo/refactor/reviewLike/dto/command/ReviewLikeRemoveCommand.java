package hwalibo.refactor.reviewLike.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewLikeRemoveCommand {
    private final Long userId;
    private final Long toiletId;
    private final Long reviewId;

    public static ReviewLikeRemoveCommand of(Long userId, Long toiletId, Long reviewId) {
        return ReviewLikeRemoveCommand.builder()
                .userId(userId)
                .toiletId(toiletId)
                .reviewId(reviewId)
                .build();
    }
}

