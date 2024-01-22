package kr.co.teacherforboss.service.AuthService;

import java.time.Duration;
import java.time.LocalDateTime;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.converter.AuthConverter;
import kr.co.teacherforboss.domain.enums.Purpose;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.PhoneAuthRepository;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.vo.mailVO.CodeMail;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.EmailAuthRepository;
import kr.co.teacherforboss.service.MailService.MailCommandService;
import lombok.RequiredArgsConstructor;
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

    // 회원 가입
    @Override
    @Transactional
    public Member joinMember(AuthRequestDTO.JoinDTO request){
        if (memberRepository.existsByEmailAndStatus(request.getEmail(), Status.ACTIVE))
            throw new MemberHandler(ErrorStatus.MEMBER_DUPLICATE);
        if (!emailAuthRepository.existsByIdAndEmailAndPurposeAndIsChecked(request.getEmailAuthId(), request.getEmail(), Purpose.of(1), "T"))
            throw new AuthHandler(ErrorStatus.MAIL_NOT_CHECKED);
        if (!request.getPassword().equals(request.getRePassword()))
            throw new AuthHandler(ErrorStatus.PASSWORD_NOT_CORRECT);
        if (!phoneAuthRepository.existsByIdAndPhoneAndPurposeAndIsChecked(request.getPhoneAuthId(), request.getPhone(), Purpose.of(1), "T"))
            throw new AuthHandler(ErrorStatus.PHONE_NOT_CHECKED);

        Member newMember = AuthConverter.toMember(request);
        String pwSalt = generateSalt();
        String pwHash = generatePwHash(request.getPassword(), pwSalt);
        newMember.setPassword(pwSalt, pwHash);

        return memberRepository.save(newMember);
    }

    // hash 생성
    private String generatePwHash(String pwRequest, String pwSalt){
        return passwordEncoder.encode(pwSalt + pwRequest);
    }

    // salt 생성
    private String generateSalt() {

        SecureRandom r = new SecureRandom();
        byte[] salt = new byte[20];

        r.nextBytes(salt);

        StringBuffer sb = new StringBuffer();
        for(byte b : salt) {
            sb.append(String.format("%02x", b));
        };

        return sb.toString();
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
    public boolean findPassword(AuthRequestDTO.FindPasswordDTO request) {
        EmailAuth emailAuth = emailAuthRepository.findById(request.getEmailAuthId())
                .orElseThrow(() -> new AuthHandler(ErrorStatus._DATA_NOT_FOUND));

        if(!emailAuthRepository.existsByIdAndPurposeAndIsChecked(request.getEmailAuthId(), Purpose.of(3), "T"))
            throw new AuthHandler(ErrorStatus.PHONE_NOT_CHECKED);

        return memberRepository.existsByEmailAndStatus(emailAuth.getEmail(), Status.ACTIVE);
    }

}