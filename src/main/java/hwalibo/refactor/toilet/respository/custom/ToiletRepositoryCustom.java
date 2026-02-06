package hwalibo.refactor.toilet.respository.custom;

import hwalibo.refactor.toilet.dto.query.StationSearchResult;
import hwalibo.refactor.toilet.dto.query.StationSuggestResult;
import hwalibo.refactor.toilet.dto.result.StationNearestResult;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ToiletRepositoryCustom {
    List<StationSearchResult> findToiletsByExactStationName(String keyword);
    List<StationNearestResult> findNearestStations(double lat, double lng);
    List<StationSuggestResult> findSuggestStations(String keyword, Pageable pageable);
}
