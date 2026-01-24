package hwalibo.refactor.global.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthenticationEntryPoint entryPoint;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // 1. 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰이 없으면 고민하지 말고 다음 필터로 고! (SecurityConfig가 알아서 판단함)
        if (!StringUtils.hasText(token)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // 3. 블랙리스트(로그아웃 토큰) 확인
            if (isBlacklisted(token)) {
                log.warn("❌ 로그아웃된 토큰입니다.");
                entryPoint.commence(request, response, new InsufficientAuthenticationException("로그아웃된 사용자입니다."));
                return;
            }

            // 4. 유효성 검사 및 인증 처리
            if (jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.warn("⚠️ 유효하지 않은 토큰입니다.");
                entryPoint.commence(request, response, new InsufficientAuthenticationException("유효하지 않은 토큰입니다."));
                return;
            }

        } catch (Exception e) {
            log.error("🔥 JWT 처리 중 오류 발생: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response, new InsufficientAuthenticationException("JWT 인증 오류"));
            return;
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
    }

    // OPTIONS 요청은 필터를 아예 안 타게 설정 (성능 이득)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getMethod().equals("OPTIONS");
    }
}