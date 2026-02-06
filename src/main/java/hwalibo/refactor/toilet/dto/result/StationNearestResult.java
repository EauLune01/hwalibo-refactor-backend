package hwalibo.refactor.toilet.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StationNearestResult {
    private Long id;
    private String name;
    private String line;
}
