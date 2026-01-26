package hwalibo.refactor.global.auth.controller;

import hwalibo.refactor.global.auth.CustomOAuth2User;
import hwalibo.refactor.global.auth.dto.command.LogoutCommand;
import hwalibo.refactor.global.auth.dto.command.ReissueTokenCommand;
import hwalibo.refactor.global.auth.dto.command.WithdrawCommand;
import hwalibo.refactor.global.auth.dto.request.ReissueTokenRequest;
import hwalibo.refactor.global.auth.dto.response.ReissueTokenResponse;
import hwalibo.refactor.global.auth.dto.result.ReissueTokenResult;
import hwalibo.refactor.global.auth.jwt.JwtConstants;
import hwalibo.refactor.global.auth.service.AuthCommandService;
import hwalibo.refactor.global.auth.service.AuthQueryService;
import hwalibo.refactor.global.dto.response.ApiResponse;
import hwalibo.refactor.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 (Auth)", description = "토큰 재발급, 로그아웃 등 사용자 인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;

    @Operation(
            summary = "토큰 재발급 (Refresh)",
            description = "만료된 Access Token과 보관 중인 Refresh Token을 사용하여 새로운 토큰 세트를 발급받습니다. Access Token이 블랙리스트에 있으면 거부됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 토큰이거나 로그아웃된 상태",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "저장소에 해당 Refresh Token이 존재하지 않음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @SecurityRequirements(value = {})
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<ReissueTokenResponse>> refreshToken(
            @RequestHeader(value = JwtConstants.HEADER_STRING, required = false) String authHeader,
            @Valid @RequestBody ReissueTokenRequest request) {

        String accessToken = extractAccessToken(authHeader);
        ReissueTokenCommand command = ReissueTokenCommand.of(accessToken, request.getRefreshToken());
        ReissueTokenResult result = authCommandService.reissue(command);
        ReissueTokenResponse response = authQueryService.getReissueResponse(result);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "토큰이 성공적으로 재발급되었습니다.", response));
    }

    @Operation(summary = "로그아웃", description = "리프레시 토큰을 삭제하고 액세스 토큰을 블랙리스트에 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", // 명세서에 204라 썼지만 ApiResponse 규격을 위해 200 추천
                    description = "로그아웃 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestHeader(value = JwtConstants.HEADER_STRING, required = false) String authHeader) {
        String accessToken = extractAccessToken(authHeader);
        User loginUser = (customOAuth2User != null) ? customOAuth2User.getUser() : null;
        LogoutCommand command = LogoutCommand.of(loginUser, accessToken);
        authCommandService.logout(command);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "성공적으로 로그아웃되었습니다.", null));
    }

    @Operation(
            summary = "회원 탈퇴 (계정 삭제)",
            description = "사용자의 계정을 탈퇴 처리합니다. 네이버 연동이 해제되며 정보는 비식별화되지만 리뷰와 이미지는 유지됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원 탈퇴 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawUser(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @RequestHeader(value = JwtConstants.HEADER_STRING, required = false) String authHeader
    ) {
        User loginUser = (customOAuth2User != null) ? customOAuth2User.getUser() : null;
        String accessToken = extractAccessToken(authHeader);
        authCommandService.withdraw(WithdrawCommand.of(loginUser, accessToken));
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "성공적으로 회원 탈퇴되었습니다.", null));
    }

    /******************** Helper Method ********************/
    private String extractAccessToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return authHeader.substring(JwtConstants.TOKEN_PREFIX.length());
        }
        return null;
    }
}