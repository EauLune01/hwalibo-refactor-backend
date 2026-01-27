package hwalibo.refactor.review.controller;

import hwalibo.refactor.global.dto.response.ApiResponse;
import hwalibo.refactor.review.dto.response.ReviewSummaryResponse;
import hwalibo.refactor.review.dto.result.ReviewSummaryResult;
import hwalibo.refactor.review.service.ReviewSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "리뷰 요약 (Review Summary)", description = "특정 화장실의 리뷰를 종합하여 요약을 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/toilet")
public class ReviewSummaryController {

    private final ReviewSummaryService reviewSummaryService;

    @Operation(
            summary = "리뷰 요약 조회",
            description = "최신 리뷰 10개를 분석하여 200바이트 이내의 한국어 요약을 반환합니다. (Redis 캐싱 적용)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "요약 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "리뷰 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "AI 생성 오류")
    })
    @GetMapping("/{toiletId}/reviews/summary")
    public ResponseEntity<ApiResponse<ReviewSummaryResponse>> summarize(@PathVariable Long toiletId) {
        ReviewSummaryResponse data = ReviewSummaryResponse.from(reviewSummaryService.summarizeByToiletId(toiletId));
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "리뷰 요약 성공", data));
    }
}
