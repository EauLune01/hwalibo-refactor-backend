package hwalibo.refactor.toilet.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StationSearchResult {
    private Long id;
    private String name;
    private String line;
    private String gender;
    private Double star;
    private Integer numReview;

    @QueryProjection
    public StationSearchResult(Long id, String name, String line, String gender, Double star, Integer numReview) {
        this.id = id;
        this.name = name;
        this.line = line;
        this.gender = gender;
        this.star = star;
        this.numReview = numReview;
    }
}
