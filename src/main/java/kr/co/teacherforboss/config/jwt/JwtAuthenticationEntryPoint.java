package kr.co.teacherforboss.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
						 AuthenticationException authException) throws IOException {
		setResponse(response, ErrorStatus._UNAUTHORIZED);
	}

	private void setResponse(HttpServletResponse response, ErrorStatus code) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		ApiResponse<Object> apiResponse = ApiResponse.onFailure(code.getCode(), code.getMessage(), null);
		response.getWriter().println(mapper.writeValueAsString(apiResponse));
	}
}
