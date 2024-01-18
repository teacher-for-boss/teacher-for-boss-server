package kr.co.teacherforboss.web.dto;

import kr.co.teacherforboss.validation.annotation.CheckPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

public class AuthRequestDTO {
    @Getter
    @Builder
    public static class JoinDTO{
        @Email
        @NotNull
        String email;

        @Pattern(regexp="T", message = "이메일을 인증받아야 합니다.")
        String isChecked;

        @NotNull
        @Size(min = 8, max = 20, message = "비밀번호를 8~20자 사이로 입력해주세요.")
        @Pattern(regexp="(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}|:<>?~,-]).{8,20}", message = "비밀번호는 숫자, 영어, 특수문자를 포함해서 8 ~ 20자리 이내로 입력해주세요.")
        String password;

        @NotNull
        String rePassword;

        @NotNull
        String name;

        Integer gender;

        LocalDate birthDate;
    }
    
    @Getter
    @AllArgsConstructor
    public static class SendCodeMailDTO {
        @NotNull(message = "email 값이 없습니다.")
        @Email(message = "email 값이 이메일 형식이 아닙니다.")
        String email;

        @CheckPurpose
        int purpose;
    }

    @Getter
    @Builder
    public static class CheckCodeMailDTO {
        @NotNull(message = "emailAuthId 값이 없습니다.")
        @ExistEmailAuth
        Long emailAuthId;

        @NotNull(message = "emailAuthCode 값이 없습니다.")
        @Pattern(regexp = "\\d{5}", message = "인증 코드는 5자리의 숫자로 이루어져 있어야 합니다.")
        String emailAuthCode;
    }
}