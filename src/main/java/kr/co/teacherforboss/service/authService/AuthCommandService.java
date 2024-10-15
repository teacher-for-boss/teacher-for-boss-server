package kr.co.teacherforboss.service.authService;

import java.util.List;
import kr.co.teacherforboss.domain.BusinessAuth;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.PhoneAuth;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;

public interface AuthCommandService {
    Member joinMember(AuthRequestDTO.JoinDTO request);
    EmailAuth sendCodeMail(AuthRequestDTO.SendCodeMailDTO request);
    boolean checkCodeMail(AuthRequestDTO.CheckCodeMailDTO request);
    PhoneAuth sendCodePhone(AuthRequestDTO.SendCodePhoneDTO request);
    boolean checkCodePhone(AuthRequestDTO.CheckCodePhoneDTO request);
    Member findPassword(AuthRequestDTO.FindPasswordDTO request);
    Member login(AuthRequestDTO.LoginDTO request);
    Member resetPassword(AuthRequestDTO.ResetPasswordDTO request);
    Member logout(String accessToken, String email);
    Member findEmail(AuthRequestDTO.FindEmailDTO request);
    Member getMember();
    List<Member> getMembers(List<Long> memberIds);
    Member socialLogin(AuthRequestDTO.SocialLoginDTO request, int socialType);
    BusinessAuth checkBusinessNumber(AuthRequestDTO.CheckBusinessNumberDTO request);
    Member withdraw();
    Member recover(String email);
    Member completeTeacherSignup(AuthRequestDTO.CompleteTeacherSignupDTO request);
}