package hwalibo.refactor.global.auth.jwt;

import hwalibo.refactor.global.exception.auth.InvalidTokenException;
import hwalibo.refactor.global.auth.CustomOAuth2User;
import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.global.domain.Role;
import hwalibo.refactor.user.domain.User;
import hwalibo.refactor.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final UserRepository userRepository;

    private static final String TOKEN_PREFIX = "Bearer ";


    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidity,
            UserRepository userRepository) {

        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.accessTokenValidityInMilliseconds = accessTokenValidity * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidity * 1000;
        this.userRepository = userRepository;
        log.info("JwtTokenProvider initialized with secret key and token validity settings.");
    }

    // Access Token 생성
    public String createAccessToken(Authentication authentication) {

        User user = extractUserFromAuthentication(authentication);
        String authorities = extractAuthorities(authentication);
        Date validity = calculateTokenValidity(this.accessTokenValidityInMilliseconds);

        return buildToken(user.getId().toString(), user.getUsername(), authorities, validity, user.getGender());
    }

    // Refresh Token 생성
    public String createRefreshToken() {
        log.info("Creating refresh token");
        Date validity = calculateTokenValidity(this.refreshTokenValidityInMilliseconds);
        return buildToken(null, null, null, validity, null);
    }

    // Authentication 객체 가져오기 (Stateless)
    public Authentication getAuthentication(String token) {
        token = stripBearerPrefix(token);
        Claims claims = parseClaimsFromToken(token);

        if (claims.get("auth") == null) {
            throw new InvalidTokenException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        String genderStr = claims.get("gender", String.class);
        Gender gender = (genderStr != null) ? Gender.valueOf(genderStr) : null;

        User principal = User.fromClaims(
                Long.parseLong(claims.getSubject()),
                claims.get("username", String.class),
                claims.get("name", String.class),
                Role.valueOf((String) authorities.iterator().next().getAuthority()),
                gender
        );

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // User로부터 Authentication 생성
    public Authentication getAuthenticationFromUser(User user) {
        log.info("Creating Authentication from user: {}", user.getUsername());
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    // JWT 토큰 검증
    public boolean validateToken(String token) {
        token = stripBearerPrefix(token);
        try {
            Jwts.parser()
                    .verifyWith(key) // setSigningKey -> verifyWith
                    .build()
                    .parseSignedClaims(token); // parseClaimsJws -> parseSignedClaims
            return true;
        } catch (JwtException e) {
            log.error("JWT token is invalid: {}", e.getMessage());
        }
        return false;
    }

    // AccessToken 남은 만료 시간(ms) 조회
    public long getRemainingTime(String token) {
        try {
            token = stripBearerPrefix(token);
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload(); // getBody -> getPayload

            Date expiration = claims.getExpiration();
            return expiration.getTime() - System.currentTimeMillis();
        } catch (JwtException e) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }

    // ---------------------- Helper Methods ----------------------

    private User extractUserFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOAuth2User oAuth2User) {
            log.info("Extracted user from CustomOAuth2User: {}", oAuth2User.getUser().getUsername());
            return oAuth2User.getUser();
        } else if (principal instanceof User user) {
            log.info("Extracted user from User: {}", user.getUsername());
            return user;
        } else {
            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
        }
    }

    private String extractAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private Date calculateTokenValidity(long validityInMilliseconds) {
        long now = (new Date()).getTime();
        return new Date(now + validityInMilliseconds);
    }

    private String buildToken(String userId, String username, String authorities, Date validity, Gender gender) {
        return Jwts.builder()
                .subject(userId) // setSubject -> subject
                .claim("username", username)
                .claim("auth", authorities)
                .claim("gender", gender != null ? gender.name() : null)
                .expiration(validity) // setExpiration -> expiration
                .signWith(key) // 알고리즘 명시 안 해도 key 타입 보고 알아서 HS256 등으로 세팅함
                .compact();
    }

    private Claims parseClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload(); // getBody() 대신 getPayload() 사용
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    private String stripBearerPrefix(String token) {
        if (token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return token;
    }
}

