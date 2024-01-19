package kr.co.teacherforboss.service.authService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.converter.AuthConverter;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.vo.mailVO.CodeMail;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.EmailAuthRepository;
import kr.co.teacherforboss.service.mailService.MailCommandService;
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
    private final MailCommandService mailCommandService;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    @Override
    @Transactional
    public Member joinMember(AuthRequestDTO.JoinDTO request){
        if (!request.getPassword().equals(request.getRePassword())) { throw new AuthHandler(ErrorStatus.PASSWORD_NOT_CORRECT);}

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
    public Member login(AuthRequestDTO.LoginDTO request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthHandler(ErrorStatus.MEMBER_NOT_FOUND));

        String inputPw =  member.getPwSalt() + request.getPassword();
        if (!passwordEncoder.matches(inputPw, member.getPwHash())) {
            throw new AuthHandler(ErrorStatus.LOGIN_FAILED_PASSWORD_INCORRECT);
        }
        return member;
    }
}