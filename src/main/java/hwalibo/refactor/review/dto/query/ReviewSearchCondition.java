package hwalibo.refactor.review.dto.query;

import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.review.domain.SortType;
import hwalibo.refactor.review.domain.Tag;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ReviewSearchCondition {

    private Gender gender;
    private List<Tag> tags;
    private Boolean hasPhotos;
    private SortType sortType;
    private Boolean onlyLiked;

    public static ReviewSearchCondition of(Gender gender, List<Tag> tags, Boolean hasPhotos, SortType sortType, Boolean onlyLiked) {
        return ReviewSearchCondition.builder()
                .gender(gender)
                .tags(tags)
                .hasPhotos(hasPhotos != null ? hasPhotos : false)
                .sortType(sortType != null ? sortType : SortType.LATEST)
                .onlyLiked(onlyLiked != null ? onlyLiked : false)
                .build();
    }
}