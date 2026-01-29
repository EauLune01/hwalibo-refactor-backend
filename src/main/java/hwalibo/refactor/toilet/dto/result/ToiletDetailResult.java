package hwalibo.refactor.toilet.dto.result;

import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.global.domain.InOut;
import hwalibo.refactor.toilet.domain.Toilet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToiletDetailResult {
    private Long id;
    private String name;
    private String line;
    private Gender gender;
    private Double star;
    private Integer numBigToilet;
    private Integer numSmallToilet;
    private Integer numGate;
    private InOut inOut;
    private Double latitude;
    private Double longitude;
    private Integer numReview;

    public static ToiletDetailResult from(Toilet toilet) {
        return ToiletDetailResult.builder()
                .id(toilet.getId())
                .name(toilet.getName())
                .line(toilet.getLine())
                .gender(toilet.getGender())
                .star(toilet.getStar())
                .numBigToilet(toilet.getNumBigToilet())
                .numSmallToilet(toilet.getNumSmallToilet())
                .numGate(toilet.getNumGate())
                .inOut(toilet.getInOut())
                .latitude(toilet.getLatitude())
                .longitude(toilet.getLongitude())
                .numReview(toilet.getNumReview())
                .build();
    }
}
