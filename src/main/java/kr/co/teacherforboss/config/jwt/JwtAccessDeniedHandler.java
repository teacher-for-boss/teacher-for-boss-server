package kr.co.teacherforboss.config.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
					   AccessDeniedException accessDeniedException) throws IOException, ServletException {
		response.sendError(HttpServletResponse.SC_FORBIDDEN);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.warn("[403 Error]: 유저({})가 접근 권한이 없는 {}에 접근했습니다.", auth.getName(), request.getRequestURI());
	}
}
