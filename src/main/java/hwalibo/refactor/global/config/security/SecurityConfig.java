package hwalibo.refactor.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwalibo.refactor.global.auth.handler.OAuth2SuccessHandler;
import hwalibo.refactor.global.auth.jwt.JwtAuthenticationFilter;
import hwalibo.refactor.global.auth.jwt.JwtTokenProvider;
import hwalibo.refactor.global.auth.service.CustomOAuth2UserService;
import hwalibo.refactor.global.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

            String errorMessage = "로그인이 필요합니다.";
            if (ex != null && ex.getMessage() != null && !ex.getMessage().equalsIgnoreCase("Full authentication is required to access this resource")) {
                errorMessage = ex.getMessage();
            }

            new ObjectMapper().writeValue(response.getWriter(), new ApiResponse<>(false, 401, errorMessage));
        };
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationEntryPoint restAuthenticationEntryPoint) {
        return new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate, restAuthenticationEntryPoint);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .cors(withDefaults())
                .httpBasic(h -> h.disable())
                .csrf(c -> c.disable())
                .formLogin(f -> f.disable())
                .logout(l -> l.disable())
                .anonymous(withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/index.html", "/auth/callback.html",
                                "/auth/refresh", "/redis/ping").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint()))
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}