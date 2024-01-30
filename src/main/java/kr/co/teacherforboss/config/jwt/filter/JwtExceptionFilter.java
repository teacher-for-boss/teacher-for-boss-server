package kr.co.teacherforboss.config.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            String message = e.getMessage();
            if(message.equals(ErrorStatus.INVALID_JWT_TOKEN.getMessage())) {
                setResponse(response, ErrorStatus.INVALID_JWT_TOKEN);
            }
            else if (message.equals(ErrorStatus.TOKEN_TIME_OUT.getMessage())) {
                setResponse(response, ErrorStatus.TOKEN_TIME_OUT);
            }
        } catch (Exception e) {
            throw new AuthHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    private void setResponse(HttpServletResponse response, ErrorStatus errorStatus)
            throws RuntimeException, IOException {

        ObjectMapper objectMapper = new JsonMapper();
        String responseJson = objectMapper.writeValueAsString(new AuthHandler(errorStatus));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        response.setStatus(errorStatus.getHttpStatus().value());
        response.getWriter().print(responseJson);
    }
}
