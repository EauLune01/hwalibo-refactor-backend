package hwalibo.refactor.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwalibo.refactor.global.auth.CustomOAuth2User;
import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.global.dto.response.ApiResponse;
import hwalibo.refactor.global.dto.response.SliceResponse;
import hwalibo.refactor.review.dto.command.ReviewCreateCommand;
import hwalibo.refactor.review.dto.command.ReviewImageUpdateCommand;
import hwalibo.refactor.review.dto.command.ReviewUpdateCommand;
import hwalibo.refactor.review.dto.request.ReviewCreateRequest;
import hwalibo.refactor.review.dto.request.ReviewImageDeleteRequest;
import hwalibo.refactor.review.dto.request.ReviewUpdateRequest;
import hwalibo.refactor.review.dto.response.*;
import hwalibo.refactor.review.dto.result.*;
import hwalibo.refactor.review.service.*;
import hwalibo.refactor.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Review", description = "화장실 리뷰 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/toilet")
public class ReviewController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;
    private final ReviewImageCommandService reviewImageCommandService;
    private final ReviewImageQueryService reviewImageQueryService;
    private final ReviewSummaryService reviewSummaryService;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "리뷰 통합 등록",
            description = "리뷰 텍스트와 이미지(0~2장)를 등록합니다. 'request'는 JSON 형식의 문자열로 보내주세요."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "리뷰 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패")
    })
    @PostMapping(value = "/{toiletId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReviewCreateResponse>> createReview(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long toiletId,
            @Parameter(
                    name = "request",
                    description = "리뷰 상세 정보 (JSON)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReviewCreateRequest.class)
                    )
            )
            @RequestPart(value = "request") String requestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        ReviewCreateRequest request = objectMapper.readValue(requestJson, ReviewCreateRequest.class);
        Long loginUserId = customOAuth2User.getUser().getId();
        Long reviewId = reviewCommandService.createReview(ReviewCreateCommand.of(request, toiletId, loginUserId), images);
        ReviewCreateResult result = reviewQueryService.getReviewCreateResult(reviewId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, 201, "리뷰 등록 성공", ReviewCreateResponse.from(result)));
    }

    @Operation(summary = "리뷰 본문 수정", description = "별점, 내용, 태그, 장애인 화장실 여부를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewUpdateResponse>> updateReview(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request) {
        User loginUser = (customOAuth2User != null) ? customOAuth2User.getUser() : null;
        reviewCommandService.updateReview(loginUser.getId(), reviewId, ReviewUpdateCommand.of(request));
        ReviewUpdateResult result = reviewQueryService.getReviewUpdateResult(reviewId);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "리뷰가 성공적으로 수정되었습니다.", ReviewUpdateResponse.from(result)));
    }

    @Operation(summary = "리뷰 이미지 수정", description = "특정 이미지를 삭제하거나 새로운 이미지를 추가하여 이미지를 교체합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "리뷰 이미지 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "이미지 개수 초과 (최대 2장) 또는 잘못된 요청 파라미터"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "수정 권한 없음 (본인 리뷰 아님)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "리뷰 정보를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500", description = "S3 업로드 오류 또는 서버 내부 오류")
    })
    @PatchMapping(value = "/reviews/{reviewId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReviewImageUpdateResponse>> updateReviewImages(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long reviewId,
            @Parameter(
                    name = "request",
                    description = "삭제할 이미지 ID 리스트 (JSON)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReviewImageDeleteRequest.class)
                    )
            )
            @RequestPart(value = "request", required = false) String deleteRequestJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) throws IOException {
        User loginUser = (customOAuth2User != null) ? customOAuth2User.getUser() : null;
        ReviewImageDeleteRequest deleteRequest = null;
        if (deleteRequestJson != null && !deleteRequestJson.isBlank()) {
            deleteRequest = objectMapper.readValue(deleteRequestJson, ReviewImageDeleteRequest.class);
        }
        reviewImageCommandService.updateImages(ReviewImageUpdateCommand.of(loginUser.getId(), reviewId, deleteRequest, photos));
        List<ReviewImageUpdateResult> updatedResults = reviewImageQueryService.getReviewImageUpdateResults(reviewId);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "이미지가 성공적으로 수정되었습니다.", ReviewImageUpdateResponse.of(reviewId, updatedResults)));
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰와 연관된 모든 이미지(S3 포함)를 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "삭제 권한 없음")
    })
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long reviewId) {
        User loginUser = (customOAuth2User != null) ? customOAuth2User.getUser() : null;
        reviewCommandService.deleteReview(loginUser.getId(), reviewId);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "리뷰가 성공적으로 삭제되었습니다.", null));
    }

    @Operation(
            summary = "포토 리뷰 모아보기",
            description = "특정 화장실에 등록된 사진들을 최신순으로 조회합니다. 사용자의 성별과 일치하는 화장실 사진만 반환됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/{toiletId}/photos")
    public ResponseEntity<ApiResponse<SliceResponse<PhotoReviewResponse>>> getPhotoReviews(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long toiletId,
            @PageableDefault(size = 3, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Gender gender = customOAuth2User.getUser().getGender();
        Slice<PhotoReviewResult> resultSlice = reviewImageQueryService.getPhotoReviews(toiletId, gender, pageable);
        Slice<PhotoReviewResponse> responseSlice = resultSlice.map(PhotoReviewResponse::from);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "포토 리뷰 목록을 성공적으로 조회했습니다.", SliceResponse.from(responseSlice)));
    }

    @Operation(
            summary = "포토 리뷰 상세 조회",
            description = "특정 사진을 클릭했을 때 사진 원본과 해당 리뷰의 상세 정보(별점, 내용, 태그 등)를 조회합니다. " +
                    "사용자의 성별과 일치하는 화장실의 리뷰만 접근 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "포토 리뷰 상세 정보를 성공적으로 조회했습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음 (성별 불일치 등)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 사진 또는 리뷰를 찾을 수 없음")
    })
    @GetMapping("/{toiletId}/photos/{photoId}")
    public ResponseEntity<ApiResponse<PhotoDetailResponse>> getPhotoDetail(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long toiletId,
            @PathVariable Long photoId) {
        PhotoDetailResult result = reviewImageQueryService.getPhotoDetail(photoId, customOAuth2User.getUser().getGender());
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "포토 리뷰 상세 정보를 성공적으로 조회했습니다.", PhotoDetailResponse.from(result)));
    }

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
    public ResponseEntity<ApiResponse<ReviewSummaryResponse>> summarize(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @PathVariable Long toiletId) {
        ReviewSummaryResponse data = ReviewSummaryResponse.from(reviewSummaryService.summarizeByToiletId(toiletId));
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "리뷰 요약 성공", data));
    }
}
