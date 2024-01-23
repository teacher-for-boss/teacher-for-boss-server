package kr.co.teacherforboss.config.jwt.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        if (request.getRequestURI().startsWith("/api/v1/auth/")) {
            log.info("[접근 권한 X] URL : [{}]", request.getRequestURL());

            ObjectMapper objectMapper = new ObjectMapper();
            String code = ErrorStatus.ACCESS_DENIED.getCode();
            String message = ErrorStatus.ACCESS_DENIED.getMessage();

            ApiResponse<ErrorStatus> apiResponse = ApiResponse.onFailure(code, message, null);
            String jsonResponse = objectMapper.writeValueAsString(apiResponse);

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(jsonResponse);
        }
    }
}