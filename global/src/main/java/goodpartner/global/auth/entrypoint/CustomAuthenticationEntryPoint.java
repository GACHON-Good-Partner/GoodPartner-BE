package goodpartner.global.auth.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import goodpartner.global.common.response.ResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static goodpartner.global.auth.exception.ErrorMessage.INVALID_TOKEN;
import static goodpartner.global.auth.exception.ErrorMessage.UNAUTHORIZED;


@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Integer exceptionCode = (Integer) request.getAttribute("jwtException");

        /*
         * exceptionCode가 null이 아니라면 토큰 유효성 검사를 실패한 것이기 때문에 따로 처리
         * exceptionCode가 null이라면 인증 정보가 없는 것이기 때문에 따로 처리
         */
        if (exceptionCode != null) {
            if (exceptionCode == INVALID_TOKEN.getCode()) {
                setResponse(response, INVALID_TOKEN.getCode(), INVALID_TOKEN.getMessage());
            }
        } else {
            setResponse(response, UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage());
        }
    }

    // 발생한 예외에 맞게 status를 설정하고 message를 반환
    private void setResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = new ObjectMapper().writeValueAsString(ResponseDto.errorResponse(code, message));
        response.getWriter().write(json);
    }

}

