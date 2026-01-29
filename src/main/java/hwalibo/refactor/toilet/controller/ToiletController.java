package hwalibo.refactor.toilet.controller;

import hwalibo.refactor.global.dto.response.ApiResponse;
import hwalibo.refactor.toilet.dto.response.ToiletDetailResponse;
import hwalibo.refactor.toilet.dto.result.ToiletDetailResult;
import hwalibo.refactor.toilet.service.ToiletQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Toilet", description = "화장실 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/toilet")
public class ToiletController {

    private final ToiletQueryService toiletQueryService;

    @Operation(
            summary = "화장실 상세 정보 조회",
            description = "특정 화장실의 ID를 이용해 화장실 이름, 호선, 위치, 시설 정보 등을 상세히 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "화장실 상세 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "해당 ID의 화장실을 찾을 수 없음")
    })
    @GetMapping("/{toiletId}")
    public ResponseEntity<ApiResponse<ToiletDetailResponse>> getToiletDetail(@PathVariable Long toiletId) {
        ToiletDetailResult result = toiletQueryService.getToiletDetail(toiletId);
        ToiletDetailResponse response = ToiletDetailResponse.from(result);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "화장실 상세 조회 성공", response));
    }
}
