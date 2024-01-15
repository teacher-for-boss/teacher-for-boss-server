package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.Gender;
import kr.co.teacherforboss.domain.enums.LoginType;
import kr.co.teacherforboss.domain.enums.Role;
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

    public static Member toMember(AuthRequestDTO.JoinDto request){
        Gender gender = switch (request.getGender()) {
            case 1 -> Gender.MALE;
            case 2 -> Gender.FEMALE;
            case 3 -> Gender.NONE;
            default -> null;
        };

        return Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .loginType(LoginType.GENERAL)
                .role(Role.USER)
                .gender(gender)
                .birthDate(request.getBirthDate())
                .build();
    }
}