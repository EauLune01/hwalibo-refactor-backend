package hwalibo.refactor.global.auth.service;

import hwalibo.refactor.global.auth.dto.command.LogoutCommand;
import hwalibo.refactor.global.auth.dto.command.ReissueTokenCommand;
import hwalibo.refactor.global.auth.dto.command.WithdrawCommand;
import hwalibo.refactor.global.auth.dto.result.ReissueTokenResult;
import hwalibo.refactor.global.exception.auth.InvalidTokenException;
import hwalibo.refactor.global.exception.auth.TokenNotFoundException;
import hwalibo.refactor.global.exception.auth.UnauthorizedException;
import hwalibo.refactor.global.exception.user.UserNotFoundException;
import hwalibo.refactor.global.auth.jwt.JwtTokenProvider;
import hwalibo.refactor.global.dto.response.TokenResponse;
import hwalibo.refactor.user.domain.User;
import hwalibo.refactor.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthCommandService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final NaverAuthService naverAuthService;

    public ReissueTokenResult reissue(ReissueTokenCommand command) {

        validateReissueConditions(command);

        User user = userRepository.findByRefreshToken(command.getRefreshToken())
                .orElseThrow(() -> new TokenNotFoundException("저장소에 Refresh Token이 존재하지 않습니다."));

        ReissueTokenResult result = jwtTokenProvider.createTokenSet(
                jwtTokenProvider.getAuthenticationFromUser(user)
        );

        user.updateRefreshToken(result.getRefreshToken());

        return result;
    }

    public void logout(LogoutCommand command) {

        validateLoginStatus(command.getUser());

        command.getUser().updateRefreshToken(null);

        processTokenBlacklist(command.getAccessToken());
    }

    public void withdraw(WithdrawCommand command) {

        validateLoginStatus(command.getUser());

        User user = userRepository.findById(command.getUser().getId())
                .orElseThrow(()->new UserNotFoundException("사용자를 찾을 수 없습니다."));

        try {
            naverAuthService.revokeNaverToken(user.getNaverRefreshToken());
        } catch (Exception e) {
            log.error("네이버 연동 해제 실패 (DB 탈퇴는 진행함): {}", e.getMessage());
        }

        user.withdrawAndAnonymize();

        processTokenBlacklist(command.getAccessToken());
    }

    /******************** Helper Method ********************/

    private void validateReissueConditions(ReissueTokenCommand command) {

        if (command.getAccessToken() != null && isBlacklisted(command.getAccessToken())) {
            throw new UnauthorizedException("로그아웃된 사용자입니다.");
        }

        if (!jwtTokenProvider.validateToken(command.getRefreshToken())) {
            throw new InvalidTokenException("유효하지 않은 Refresh Token 입니다.");
        }
    }

    private boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + accessToken));
    }

    private void registerBlacklist(String accessToken, String actionType) {
        long remainingMillis = jwtTokenProvider.getRemainingTime(accessToken);
        if (remainingMillis > 0) {
            redisTemplate.opsForValue().set(
                    "blacklist:" + accessToken,
                    actionType,
                    remainingMillis,
                    TimeUnit.MILLISECONDS
            );
            log.info("Access Token 블랙리스트 등록: {} (만료까지 {}ms)", actionType, remainingMillis);
        } else {
            log.debug("만료된 토큰이라 블랙리스트 등록 생략");
        }
    }

    private void validateLoginStatus(User user) {
        if (user == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
    }

    private void processTokenBlacklist(String accessToken) {
        if (accessToken != null) {
            registerBlacklist(accessToken, "logout");
        }
    }
}