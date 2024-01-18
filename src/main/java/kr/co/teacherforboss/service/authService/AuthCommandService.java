package kr.co.teacherforboss.service.authService;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;

public interface AuthCommandService {
    Member joinMember(AuthRequestDTO.JoinDTO request);
    EmailAuth sendCodeMail(AuthRequestDTO.SendCodeMailDTO request);
    Member login(AuthRequestDTO.LoginDTO request);
}