package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.enums.Gender;
import kr.co.teacherforboss.domain.enums.LoginType;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Purpose;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.web.dto.AuthResponseDTO;

import java.security.SecureRandom;
import java.time.LocalDateTime;

public class AuthConverter {

    public static AuthResponseDTO.JoinResultDTO toJoinResultDTO(Member member){
        return AuthResponseDTO.JoinResultDTO.builder()
                .memberId(member.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Member toMember(AuthRequestDTO.JoinDTO request){
        Gender gender = switch (request.getGender()) {
            case 1 -> Gender.MALE;
            case 2 -> Gender.FEMALE;
            default -> Gender.NONE;
        };

        return Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .loginType(LoginType.GENERAL)
                .role(Role.USER)
                .gender(gender)
                .birthDate(request.getBirthDate())
                .phone(request.getPhone())
                .build();
    }
    
    public static AuthResponseDTO.SendCodeMailResultDTO toSendCodeMailResultDTO(EmailAuth emailAuth) {
        return AuthResponseDTO.SendCodeMailResultDTO.builder()
                .emailAuthId(emailAuth.getId())
                .createdAt(emailAuth.getCreatedAt())
                .build();
    }

    public static EmailAuth toEmailAuth(AuthRequestDTO.SendCodeMailDTO request) {
        return EmailAuth.builder()
                .email(request.getEmail())
                .purpose(Purpose.of(request.getPurpose()))
                .isChecked("F")
                .build();
    }

    public static AuthResponseDTO.CheckCodeMailResultDTO toCheckCodeMailResultDTO(boolean isChecked) {
        return AuthResponseDTO.CheckCodeMailResultDTO.builder()
                .isChecked(isChecked)
                .build();
    }

    public static AuthResponseDTO.FindPasswordResultDTO toFindPasswordResultDTO(boolean emailChecked) {
        if (!emailChecked) throw new AuthHandler(ErrorStatus.MEMBER_NOT_FOUND);
        return AuthResponseDTO.FindPasswordResultDTO.builder()
                .passwordResult("비밀번호 찾기에 필요한 이메일 인증이 완료되었습니다.")
                .build();
    }
}