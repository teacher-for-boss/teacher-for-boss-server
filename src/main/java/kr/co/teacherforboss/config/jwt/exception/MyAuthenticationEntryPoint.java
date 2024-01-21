package kr.co.teacherforboss.config.jwt.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        if (request.getRequestURI().startsWith("/api/v1/auth/")) {
            log.info("[로그인 X] MyAuthenticationEntryPoint");

            ObjectMapper objectMapper = new ObjectMapper();
            String code = ErrorStatus.INVALID_JWT_TOKEN.getCode();
            String message = ErrorStatus.INVALID_JWT_TOKEN.getMessage();

            ApiResponse<ErrorStatus> apiResponse = ApiResponse.onFailure(code, message, null);
            String jsonResponse = objectMapper.writeValueAsString(apiResponse);

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(jsonResponse);
        }
    }
}
