package kr.co.teacherforboss.service.authService;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.PhoneAuth;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.web.dto.AuthResponseDTO;

public interface AuthCommandService {
    Member joinMember(AuthRequestDTO.JoinDTO request);
    EmailAuth sendCodeMail(AuthRequestDTO.SendCodeMailDTO request);
    boolean checkCodeMail(AuthRequestDTO.CheckCodeMailDTO request);
    PhoneAuth sendCodePhone(AuthRequestDTO.SendCodePhoneDTO request);
    boolean checkCodePhone(AuthRequestDTO.CheckCodePhoneDTO request);
    Member findPassword(AuthRequestDTO.FindPasswordDTO request);
    Member login(AuthRequestDTO.LoginDTO request);
    Member resetPassword(AuthRequestDTO.ResetPasswordDTO request);
    AuthResponseDTO.LogoutResultDTO logout(String accessToken, String email);
    Member findEmail(AuthRequestDTO.FindEmailDTO request);
    Member getMember();
    Member socialLogin(AuthRequestDTO.SocialLoginDTO request, int socialType);
    boolean checkBusinessNumber(AuthRequestDTO.CheckBusinessNumberDTO request);
}