package kr.co.teacherforboss.util;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

	private SecurityUtil() {
	}

	public static String getCurrentUserEmail() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new GeneralException(ErrorStatus.INVALID_JWT_TOKEN);
		}

		return authentication.getName();
	}
}
