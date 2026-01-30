package hwalibo.refactor.toilet.dto.command;

import hwalibo.refactor.toilet.dto.request.StationNearestRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StationNearestCommand {
    private double latitude;
    private double longitude;

    public static StationNearestCommand from(StationNearestRequest request) {
        return StationNearestCommand.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }
}
