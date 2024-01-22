package kr.co.teacherforboss.service.authService;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.web.dto.AuthResponseDTO;

public interface AuthCommandService {
    Member joinMember(AuthRequestDTO.JoinDTO request);
    EmailAuth sendCodeMail(AuthRequestDTO.SendCodeMailDTO request);
    boolean checkCodeMail(AuthRequestDTO.CheckCodeMailDTO request);
    Member login(AuthRequestDTO.LoginDTO request);
    AuthResponseDTO.LogoutResultDTO logout(String accessToken, String email);
}