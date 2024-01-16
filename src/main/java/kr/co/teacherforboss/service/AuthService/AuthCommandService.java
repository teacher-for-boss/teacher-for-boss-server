package kr.co.teacherforboss.service.AuthService;

import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;

public interface AuthCommandService {
    EmailAuth sendCodeMail(AuthRequestDTO.SendCodeMailDTO request);
}
