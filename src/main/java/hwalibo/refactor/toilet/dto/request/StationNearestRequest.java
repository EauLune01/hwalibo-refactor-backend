package hwalibo.refactor.toilet.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationNearestRequest {

    @NotNull(message = "위도(latitude)는 필수 입력 값입니다.")
    private Double latitude;

    @NotNull(message = "경도(longitude)는 필수 입력 값입니다.")
    private Double longitude;
}
