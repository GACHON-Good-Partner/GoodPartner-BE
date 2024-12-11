package goodpartner.global.auth.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static goodpartner.global.auth.exception.ErrorMessage.INVALID_TOKEN;


@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);

        try {
            if (token != null) {
                jwtProvider.validateToken(token);

                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {
            log.info("error token: {}", e.getMessage());
            request.setAttribute("jwtException", INVALID_TOKEN.getCode());
        }
        filterChain.doFilter(request, response);
    }

    // request의 헤더에 Authorization에서 토큰을 추출하고 Bearer 뒷 부분 JWT 토큰만 분리하여 반환
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            // "Bearer "를 뺀 부분만 반환
            return token.substring(7);
        }
        return null;
    }
}