package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class AuthRequestDTO {

    @Getter
    @AllArgsConstructor
    public static class SendCodeMailDTO {
        @NotNull(message = "email 값이 없습니다.")
        @Email(message = "email 값이 이메일 형식이 아닙니다.")
        String email;
    }
}
