package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.NotBlank;
import kr.co.teacherforboss.validation.annotation.CheckPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.co.teacherforboss.validation.annotation.CheckSocialType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

public class AuthRequestDTO {
    @Getter
    @Builder
    public static class JoinDTO{
        @Email
        @NotNull
        String email;

        @NotNull
        Long emailAuthId;

        @NotNull
        Long phoneAuthId;

        @NotNull
        @Size(min = 8, max = 20, message = "비밀번호를 8~20자 사이로 입력해주세요.")
        @Pattern(regexp="(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}|:<>?~,-]).{8,20}", message = "비밀번호는 숫자, 영어, 특수문자를 포함해서 8 ~ 20자리 이내로 입력해주세요.")
        String password;

        @NotNull
        String rePassword;

        @NotNull
        String name;

        @NotNull
        @Pattern(regexp = "010([2-9])\\d{7,8}", message = "전화번호는 10 ~ 11 자리의 숫자만 입력 가능합니다.")
        String phone;

        Integer gender;

        LocalDate birthDate;
    }
    
    @Getter
    @Builder
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
        Long emailAuthId;

        @NotNull(message = "emailAuthCode 값이 없습니다.")
        @Pattern(regexp = "\\d{5}", message = "인증 코드는 5자리의 숫자로 이루어져 있어야 합니다.")
        String emailAuthCode;
    }

    @Getter
    @Builder
    public static class SendCodePhoneDTO {
        @NotNull(message = "phone 값이 없습니다.")
        @Pattern(regexp = "010([2-9])\\d{7,8}", message = "전화번호는 10 ~ 11 자리의 숫자만 입력 가능합니다.")
        String phone;

        @CheckPurpose
        int purpose;

        @Pattern(regexp = "^[a-zA-Z0-9]{11}$", message = "앱 해시는 11자리의 영문, 숫자로 이루어져 있어야 합니다.")
        String appHash;
    }

    @Getter
    @Builder
    public static class CheckCodePhoneDTO {
        @NotNull(message = "phoneAuthId 값이 없습니다.")
        Long phoneAuthId;

        @NotNull(message = "phoneAuthCode 값이 없습니다.")
        @Pattern(regexp = "\\d{4}", message = "인증 코드는 4자리의 숫자로 이루어져 있어야 합니다.")
        String phoneAuthCode;
    }

    @Getter
    @Jacksonized
    @Builder
    public static class FindEmailDTO {
        @NotNull(message = "phoneAuthId 값이 없습니다.")
        Long phoneAuthId;
    }

    @Getter
    @Jacksonized
    @Builder
    public static class FindPasswordDTO {
        @NotNull(message = "emailAuthId 값이 없습니다.")
        Long emailAuthId;
    }

    @Getter
    public static class LoginDTO {
        @NotBlank
        String email;

        @NotBlank
        String password;
    }

    @Getter
    public static class ResetPasswordDTO {
        @NotNull(message = "memberId 값이 없습니다.")
        Long memberId;

        @NotNull
        @Size(min = 8, max = 20, message = "비밀번호를 8~20자 사이로 입력해주세요.")
        @Pattern(regexp="(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}|:<>?~,-]).{8,20}", message = "비밀번호는 숫자, 영어, 특수문자를 포함해서 8 ~ 20자리 이내로 입력해주세요.")
        String password;

        @NotNull
        @Size(min = 8, max = 20, message = "비밀번호를 8~20자 사이로 입력해주세요.")
        String rePassword;
    }

    @Getter
    @Builder
    public static class SocialLoginDTO {

        @CheckSocialType
        int socialType;

        @Email
        @NotNull
        String email;

        String name;

        @Pattern(regexp = "010([2-9])\\d{7,8}", message = "전화번호는 10 ~ 11 자리의 숫자만 입력 가능합니다.")
        String phone;

        Integer gender;

        LocalDate birthDate;

        String profileImg;
    }
}
