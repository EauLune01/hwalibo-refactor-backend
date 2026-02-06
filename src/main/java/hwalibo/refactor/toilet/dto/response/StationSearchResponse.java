package hwalibo.refactor.toilet.dto.response;

import hwalibo.refactor.toilet.dto.query.StationSearchResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StationSearchResponse {
    private Long id;
    private String name;
    private String line;
    private String gender;
    private Double star;
    private Integer numReview;

    public static StationSearchResponse from(StationSearchResult result) {
        return StationSearchResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .line(result.getLine())
                .gender(result.getGender())
                .star(result.getStar())
                .numReview(result.getNumReview())
                .build();
    }
}
