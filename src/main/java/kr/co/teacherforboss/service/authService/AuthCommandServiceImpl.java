package kr.co.teacherforboss.service.authService;

import java.time.Duration;
import java.time.LocalDateTime;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.config.jwt.PrincipalDetails;
import kr.co.teacherforboss.config.jwt.TokenManager;
import kr.co.teacherforboss.converter.AuthConverter;
import kr.co.teacherforboss.util.PasswordUtil;
import kr.co.teacherforboss.domain.PhoneAuth;
import kr.co.teacherforboss.domain.enums.Purpose;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.PhoneAuthRepository;
import kr.co.teacherforboss.domain.vo.smsVO.CodeSMS;
import kr.co.teacherforboss.util.SmsUtil;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.vo.mailVO.CodeMail;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.EmailAuthRepository;
import kr.co.teacherforboss.service.mailService.MailCommandService;
import kr.co.teacherforboss.web.dto.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PhoneAuthRepository phoneAuthRepository;
    private final MailCommandService mailCommandService;
    private final PasswordEncoder passwordEncoder;
    private final TokenManager tokenManager;
    private final SmsUtil smsUtil;
    private final PasswordUtil passwordUtil;

    // 회원 가입
    @Override
    @Transactional
    public Member joinMember(AuthRequestDTO.JoinDTO request){
        if (!request.getPassword().equals(request.getRePassword())) { throw new AuthHandler(ErrorStatus.PASSWORD_NOT_CORRECT);}

        Member newMember = AuthConverter.toMember(request);
        passwordUtil.setMemberPassword(newMember, request.getPassword());

        return memberRepository.save(newMember);
    }

    @Override
    @Transactional
    public EmailAuth sendCodeMail(AuthRequestDTO.SendCodeMailDTO request) {
        String to = request.getEmail();

        // TODO: 하루 이메일 인증 5회 제한 확인

        CodeMail codeMail = new CodeMail();
        mailCommandService.sendMail(to, codeMail);

        EmailAuth emailAuth = AuthConverter.toEmailAuth(request);
        emailAuth.setCode(codeMail.getValues().get("code"));

        return emailAuthRepository.save(emailAuth);
    }

    @Override
    @Transactional
    public boolean checkCodeMail(AuthRequestDTO.CheckCodeMailDTO request) {

        EmailAuth emailAuth = emailAuthRepository.findById(request.getEmailAuthId())
                .orElseThrow(() -> new AuthHandler(ErrorStatus._DATA_NOT_FOUND));

        boolean codeCheck = request.getEmailAuthCode().equals(emailAuth.getCode());
        if (!codeCheck) throw new AuthHandler(ErrorStatus.INVALID_CODE_MAIL);

        boolean timeCheck = Duration.between(emailAuth.getCreatedAt(), LocalDateTime.now()).getSeconds() < CodeMail.VALID_TIME;
        if (!timeCheck) throw new AuthHandler(ErrorStatus.TIMEOUT_CODE_MAIL);

        emailAuth.setIsChecked(true);
        return true;
    }

    @Override
    @Transactional
    public PhoneAuth sendCodePhone(AuthRequestDTO.SendCodePhoneDTO request) {
        String to = request.getPhone();

        // TODO: 하루 휴대폰 인증 5회 제한 확인

        CodeSMS codeSMS = new CodeSMS(request.getAppHash());
        smsUtil.sendOne(to, codeSMS);

        PhoneAuth phoneAuth = AuthConverter.toPhoneAuth(request);
        phoneAuth.setCode(codeSMS.getCode());

        return phoneAuthRepository.save(phoneAuth);
    }

    @Override
    @Transactional
    public Member findPassword(AuthRequestDTO.FindPasswordDTO request) {
        EmailAuth emailAuth = emailAuthRepository.findById(request.getEmailAuthId())
                .orElseThrow(() -> new AuthHandler(ErrorStatus._DATA_NOT_FOUND));

        if(!emailAuthRepository.existsByIdAndPurposeAndIsChecked(request.getEmailAuthId(), Purpose.of(3), "T"))
            throw new AuthHandler(ErrorStatus.PHONE_NOT_CHECKED);

        return memberRepository.findByEmailAndStatus(emailAuth.getEmail(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    public Member login(AuthRequestDTO.LoginDTO request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthHandler(ErrorStatus.MEMBER_NOT_FOUND));

        String inputPw =  member.getPwSalt() + request.getPassword();
        if (!passwordEncoder.matches(inputPw, member.getPwHash())) {
            throw new AuthHandler(ErrorStatus.LOGIN_FAILED_PASSWORD_INCORRECT);
        }
        return member;
    }

    @Override
    public AuthResponseDTO.LogoutResultDTO logout(String accessToken, String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || email.isEmpty()) {
            throw new AuthHandler(ErrorStatus.INVALID_JWT_TOKEN);
        }

        tokenManager.deleteRefreshToken(email);
        tokenManager.addBlackListAccessToken(accessToken, email);

        return AuthConverter.toLogoutResultDTO(email, accessToken);
    }

    @Override
    @Transactional
    public Member findEmail(AuthRequestDTO.FindEmailDTO request) {
        PhoneAuth phoneAuth = phoneAuthRepository.findById(request.getPhoneAuthId())
                .orElseThrow(() -> new AuthHandler(ErrorStatus._DATA_NOT_FOUND));

        if(!phoneAuthRepository.existsByIdAndPurposeAndIsChecked(request.getPhoneAuthId(), Purpose.of(2), "T"))
            throw new AuthHandler(ErrorStatus.PHONE_NOT_CHECKED);

        return memberRepository.findByPhoneAndStatus(phoneAuth.getPhone(), Status.ACTIVE);
    }

    @Override
    @Transactional
    public Member resetPassword(AuthRequestDTO.resetPasswordDTO request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (!request.getPassword().equals(request.getRePassword()))
            throw new AuthHandler(ErrorStatus.PASSWORD_NOT_CORRECT);

        passwordUtil.setMemberPassword(member, request.getRePassword());

        return memberRepository.save(member);
    }
}