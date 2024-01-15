package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class AuthRequestDTO {
    @Getter
    @Builder
    public static class JoinDto{
        @Email @NotNull
        String email;
        String isChecked;
        @NotNull
        String password;
        @NotNull
        String rePassword;
        @NotNull
        String name;
        Integer gender;
        LocalDate birthDate;
    }
}
