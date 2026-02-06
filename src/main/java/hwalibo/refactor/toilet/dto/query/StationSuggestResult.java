package hwalibo.refactor.toilet.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StationSuggestResult {
    private Long id;
    private String name;
    private String line;

    @QueryProjection
    public StationSuggestResult(Long id, String name, String line) {
        this.id = id;
        this.name = name;
        this.line = line;
    }
}
