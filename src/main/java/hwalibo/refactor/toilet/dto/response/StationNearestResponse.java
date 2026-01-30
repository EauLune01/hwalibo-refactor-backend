package hwalibo.refactor.toilet.dto.response;

import hwalibo.refactor.toilet.dto.result.StationNearestResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StationNearestResponse {
    private Long id;
    private String stationName;

    public static StationNearestResponse from(StationNearestResult result) {
        return StationNearestResponse.builder()
                .id(result.getId())
                .stationName(result.getName() + " (" + result.getLine() + "호선)")
                .build();
    }
}
