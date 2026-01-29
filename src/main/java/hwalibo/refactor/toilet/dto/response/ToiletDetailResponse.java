package hwalibo.refactor.toilet.dto.response;

import hwalibo.refactor.toilet.dto.result.ToiletDetailResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToiletDetailResponse {
    private Long id;
    private String name;
    private String line;
    private String gender;
    private Double star;
    private Integer numBigToilet;
    private Integer numSmallToilet;
    private Integer numGate;
    private String inOut;
    private Double latitude;
    private Double longitude;
    private Integer numReview;

    public static ToiletDetailResponse from(ToiletDetailResult result) {
        return ToiletDetailResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .line(result.getLine())
                .gender(result.getGender() != null ? result.getGender().name() : null)
                .star(result.getStar())
                .numBigToilet(result.getNumBigToilet())
                .numSmallToilet(result.getNumSmallToilet())
                .numGate(result.getNumGate())
                .inOut(result.getInOut() != null ? result.getInOut().name() : null)
                .latitude(result.getLatitude())
                .longitude(result.getLongitude())
                .numReview(result.getNumReview())
                .build();
    }
}
