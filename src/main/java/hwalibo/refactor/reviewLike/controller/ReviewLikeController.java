package hwalibo.refactor.reviewLike.controller;

import hwalibo.refactor.global.auth.CustomOAuth2User;
import hwalibo.refactor.global.dto.response.ApiResponse;
import hwalibo.refactor.reviewLike.dto.command.ReviewLikeAddCommand;
import hwalibo.refactor.reviewLike.dto.command.ReviewLikeRemoveCommand;
import hwalibo.refactor.reviewLike.service.ReviewLikeCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리뷰 좋아요", description = "리뷰 좋아요 추가 및 취소 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/toilet/{toiletId}/reviews/{reviewId}/like")
public class ReviewLikeController {

    private final ReviewLikeCommandService reviewLikeCommandService;

    @Operation(
            summary = "리뷰 좋아요 추가",
            description = "특정 리뷰에 좋아요를 누릅니다. 이미 누른 경우 409 에러가 발생합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "리뷰 좋아요 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "리뷰/사용자 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 좋아요를 누른 리뷰")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addLike(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long toiletId,
            @PathVariable Long reviewId) {
        Long userId = customOAuth2User.getUser().getId();
        reviewLikeCommandService.addLike(ReviewLikeAddCommand.of(userId,toiletId,reviewId));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, 201, "리뷰 좋아요 성공", null));
    }

    @Operation(
            summary = "리뷰 좋아요 취소",
            description = "특정 리뷰에 누른 좋아요를 취소합니다. 누르지 않은 상태면 409 에러가 발생합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰 좋아요 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "리뷰/사용자 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "좋아요를 누르지 않은 리뷰")
    })
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeLike(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long toiletId,
            @PathVariable Long reviewId) {
        Long userId =customOAuth2User.getUser().getId();
        reviewLikeCommandService.removeLike(ReviewLikeRemoveCommand.of(userId, toiletId,reviewId));
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "리뷰 좋아요 취소 성공", null));
    }
}