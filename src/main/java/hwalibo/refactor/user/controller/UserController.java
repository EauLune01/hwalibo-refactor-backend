package hwalibo.refactor.user.controller;

import hwalibo.refactor.global.auth.CustomOAuth2User;
import hwalibo.refactor.global.dto.response.ApiResponse;
import hwalibo.refactor.global.dto.response.SliceResponse;
import hwalibo.refactor.user.domain.User;
import hwalibo.refactor.user.dto.command.UserNameUpdateCommand;
import hwalibo.refactor.user.dto.request.UserNameUpdateRequest;
import hwalibo.refactor.user.dto.response.UserResponse;
import hwalibo.refactor.user.dto.response.UserReviewResponse;
import hwalibo.refactor.user.dto.result.UserReviewResult;
import hwalibo.refactor.user.service.UserCommandService;
import hwalibo.refactor.user.service.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 (User)", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인된 사용자의 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> profile(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        User loginUser = (customOAuth2User != null) ? customOAuth2User.getUser() : null;
        UserResponse data = UserResponse.from(userQueryService.getUserInfo(loginUser));
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "내 정보 조회 성공", data));
    }

    @Operation(
            summary = "사용자 이름 수정",
            description = "현재 로그인된 사용자의 닉네임을 수정합니다. 이름은 2자 이상 10자 이하로 입력해주세요."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "사용자 이름이 성공적으로 수정되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 입력이거나 현재 닉네임과 동일함"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증이 필요합니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 존재하는 닉네임입니다.")
    })
    @PatchMapping("/profile/name")
    public ResponseEntity<ApiResponse<Void>> updateName(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody UserNameUpdateRequest request) {
        User loginUser = (customOAuth2User != null) ? customOAuth2User.getUser() : null;
        userCommandService.updateUserName(loginUser, UserNameUpdateCommand.of(loginUser.getId(), request.getName()));
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "사용자 이름이 성공적으로 수정되었습니다.", null));
    }

    @Operation(
            summary = "내 리뷰 목록 조회",
            description = "현재 로그인된 사용자가 작성한 리뷰 목록을 10개씩 페이징하여 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내 리뷰 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<SliceResponse<UserReviewResponse>>> getMyReviews(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Long userId = (customOAuth2User != null) ? customOAuth2User.getUser().getId() : null;
        Slice<UserReviewResult> resultSlice = userQueryService.getUserReviews(userId, pageable);
        Slice<UserReviewResponse> responseSlice = resultSlice.map(UserReviewResponse::from);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "리뷰 목록을 성공적으로 조회했습니다.", SliceResponse.from(responseSlice)));
    }
}
