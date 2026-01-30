package hwalibo.refactor.toilet.controller;

import hwalibo.refactor.global.auth.CustomOAuth2User;
import hwalibo.refactor.global.dto.response.ApiResponse;
import hwalibo.refactor.toilet.dto.command.StationNearestCommand;
import hwalibo.refactor.toilet.dto.request.StationNearestRequest;
import hwalibo.refactor.toilet.dto.response.StationNearestResponse;
import hwalibo.refactor.toilet.dto.response.StationSearchResponse;
import hwalibo.refactor.toilet.dto.response.StationSuggestResponse;
import hwalibo.refactor.toilet.service.StationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.List;

@Tag(name = "Station", description = "역 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationController {

    private final StationQueryService stationQueryService;

    @Operation(
            summary = "역 이름 자동 완성",
            description = "입력한 키워드로 시작하는 역 이름을 최대 10개 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "자동 완성 목록 조회 성공")
    })
    @GetMapping("/suggest")
    public ResponseEntity<ApiResponse<List<StationSuggestResponse>>> suggest(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestParam String keyword) {

        List<StationSuggestResponse> responses = stationQueryService.suggestStations(keyword)
                .stream()
                .map(StationSuggestResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "역 자동 완성 목록 조회 성공", responses));
    }

    @Operation(summary = "역 기반 화장실 검색", description = "입력한 키워드와 정확히 일치하는 역의 화장실 정보를 반환합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StationSearchResponse>>> search(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestParam("q") String q) {

        List<StationSearchResponse> responses = stationQueryService.searchToiletsByStation(q)
                .stream()
                .map(StationSearchResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "검색 성공", responses));
    }

    @Operation(
            summary = "가까운 지하철역 조회",
            description = "현재 위치(위도, 경도)를 JSON 바디로 받아 가장 가까운 역 3개를 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @PostMapping("/nearest-station")
    public ResponseEntity<ApiResponse<List<StationNearestResponse>>> getNearest(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody StationNearestRequest request) {

        StationNearestCommand command = StationNearestCommand.from(request);
        List<StationNearestResponse> responses = stationQueryService.getNearestStations(command)
                .stream()
                .map(StationNearestResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "가까운 역 조회 성공", responses));
    }
}

