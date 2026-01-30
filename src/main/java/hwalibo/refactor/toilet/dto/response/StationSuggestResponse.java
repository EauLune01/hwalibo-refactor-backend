package hwalibo.refactor.toilet.dto.response;

import hwalibo.refactor.toilet.dto.result.StationSuggestResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StationSuggestResponse {
    private Long id;
    private String stationName;

    public static StationSuggestResponse from(StationSuggestResult result) {
        return StationSuggestResponse.builder()
                .id(result.getId())
                .stationName(result.getName() + " (" + result.getLine() + "호선)")
                .build();
    }
}
