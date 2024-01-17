package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.enums.Purpose;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.web.dto.AuthResponseDTO;

public class AuthConverter {

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
}
