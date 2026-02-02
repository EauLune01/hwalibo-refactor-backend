package hwalibo.refactor.toilet.service;

import hwalibo.refactor.toilet.dto.command.StationNearestCommand;
import hwalibo.refactor.toilet.dto.response.StationSuggestResponse;
import hwalibo.refactor.toilet.dto.result.StationNearestResult;
import hwalibo.refactor.toilet.dto.result.StationSearchResult;
import hwalibo.refactor.toilet.dto.result.StationSuggestResult;
import hwalibo.refactor.toilet.respository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationQueryService {

    private final ToiletRepository toiletRepository;

    public List<StationSuggestResult> suggestStations(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        return toiletRepository.findSuggestStations(keyword, PageRequest.of(0, 10));
    }

    public List<StationSearchResult> searchToiletsByStation(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        return toiletRepository.findToiletsByExactStationName(keyword);
    }

    public List<StationNearestResult> getNearestStations(StationNearestCommand command) {
        return toiletRepository.findNearestStations(
                command.getLatitude(),
                command.getLongitude()
        );
    }
}
