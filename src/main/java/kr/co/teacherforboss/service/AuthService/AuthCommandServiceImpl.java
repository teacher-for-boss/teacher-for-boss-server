package kr.co.teacherforboss.service.AuthService;

import jakarta.transaction.Transactional;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.converter.AuthConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import static kr.co.teacherforboss.util.ValidationRegex.isRegexPassword;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    @Override
    @Transactional
    public Member joinMember(AuthRequestDTO.JoinDto request){
        if (request.getIsChecked().equals("F")) { throw new AuthHandler(ErrorStatus.EMAIL_NOT_CHECKED);}
        if (!isRegexPassword(request.getPassword())) {throw new AuthHandler(ErrorStatus.INVALID_PASSWORD);}
        if (!request.getPassword().equals(request.getRePassword())) { throw new AuthHandler(ErrorStatus.PASSWORD_NOT_CORRECT);}

        Member newMember = AuthConverter.toMember(request);
        String pwSalt = getSalt();
        String pwHash = getPassword(request.getPassword(), pwSalt);
        newMember.setPassword(pwSalt, pwHash);

        return memberRepository.save(newMember);
    }

    // hash 생성
    public String getPassword(String pwRequest, String pwSalt){
        return passwordEncoder.encode(pwSalt + pwRequest);
    }

    // salt 생성
    public String getSalt() {

        SecureRandom r = new SecureRandom();
        byte[] salt = new byte[20];

        r.nextBytes(salt);

        StringBuffer sb = new StringBuffer();
        for(byte b : salt) {
            sb.append(String.format("%02x", b));
        };

        return sb.toString();
    }

}