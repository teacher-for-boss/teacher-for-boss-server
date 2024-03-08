package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.NotBlank;
import kr.co.teacherforboss.validation.annotation.CheckPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.co.teacherforboss.validation.annotation.CheckTrueOrFalse;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

public class AuthRequestDTO {
    @Getter
    @Builder
    public static class JoinDTO{
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotNull(message = "이메일은 필수 입력값입니다.")
        String email;

        @NotNull(message = "이메일 인증 식별자는 필수 입력값입니다.")
        Long emailAuthId;

        @NotNull(message = "전화번호 인증 식별자는 필수 입력값입니다.")
        Long phoneAuthId;

        @NotNull(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호를 8~20자 사이로 입력해주세요.")
        @Pattern(regexp="(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}|:<>?~,-]).{8,20}", message = "비밀번호는 숫자, 영어, 특수문자를 포함해서 8 ~ 20자리 이내로 입력해주세요.")
        String password;

        @NotNull(message = "비밀번호 확인은 필수 입력값입니다.")
        String rePassword;

        @NotNull(message = "사용자 이름은 필수 입력값입니다.")
        String name;

        @NotNull(message = "전화번호는 필수 입력값입니다.")
        @Pattern(regexp = "010([2-9])\\d{7,8}", message = "전화번호는 10 ~ 11 자리의 숫자만 입력 가능합니다.")
        String phone;

        Integer gender;

        LocalDate birthDate;

        @NotNull(message = "이용 정보 약관 동의는 필수여야 합니다.")
        @CheckTrueOrFalse
        String agreementUsage;

        @NotNull(message = "개인 정보 약관 동의는 필수여야 합니다.")
        @CheckTrueOrFalse
        String agreementInfo;

        @NotNull(message = "14세 이상 약관 동의는 필수여야 합니다.")
        @CheckTrueOrFalse
        String agreementAge;

        @NotNull(message = "SMS 수신 동의 여부를 입력해주세요.")
        @CheckTrueOrFalse
        String agreementSms;

        @NotNull(message = "이메일 수신 동의 여부를 입력해주세요.")
        @CheckTrueOrFalse
        String agreementEmail;

        @NotNull(message = "위치 이용 동의 여부를 입력해주세요.")
        @CheckTrueOrFalse
        String agreementLocation;
    }
    
    @Getter
    @Builder
    public static class SendCodeMailDTO {
        @NotNull(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email;

        @CheckPurpose
        int purpose;
    }

    @Getter
    @Builder
    public static class CheckCodeMailDTO {
        @NotNull(message = "이메일 인증 식별자는 필수 입력값입니다.")
        Long emailAuthId;

        @NotNull(message = "이메일 인증 코드는 필수 입력값입니다.")
        @Pattern(regexp = "\\d{5}", message = "인증 코드는 5자리의 숫자로 이루어져 있어야 합니다.")
        String emailAuthCode;
    }

    @Getter
    @Builder
    public static class SendCodePhoneDTO {
        @NotNull(message = "전화번호는 필수 입력값입니다.")
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
        @NotNull(message = "전화번호 인증 식별자는 필수 입력값입니다.")
        Long phoneAuthId;

        @NotNull(message = "전화번호 인증 코드는 필수 입력값입니다.")
        @Pattern(regexp = "\\d{5}", message = "인증 코드는 4자리의 숫자로 이루어져 있어야 합니다.")
        String phoneAuthCode;
    }

    @Getter
    @Jacksonized
    @Builder
    public static class FindEmailDTO {
        @NotNull(message = "전화번호 인증 식별자는 필수 입력값입니다.")
        Long phoneAuthId;
    }

    @Getter
    @Jacksonized
    @Builder
    public static class FindPasswordDTO {
        @NotNull(message = "이메일 인증 식별자는 필수 입력값입니다.")
        Long emailAuthId;
    }

    @Getter
    public static class LoginDTO {
        @NotBlank(message = "이메일 값은 필수 입력값입니다.")
        String email;

        @NotBlank(message = "비밀번호 값은 필수 입력값입니다.")
        String password;
    }

    @Getter
    public static class ResetPasswordDTO {
        @NotNull(message = "사용자 식별자 값은 필수 입력값입니다.")
        Long memberId;

        @NotNull(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호를 8~20자 사이로 입력해주세요.")
        @Pattern(regexp="(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}|:<>?~,-]).{8,20}", message = "비밀번호는 숫자, 영어, 특수문자를 포함해서 8 ~ 20자리 이내로 입력해주세요.")
        String password;

        @NotNull(message = "비밀번호 확인은 필수 입력값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호를 8~20자 사이로 입력해주세요.")
        String rePassword;
    }

    @Getter
    @Builder
    public static class SocialLoginDTO {

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotNull(message = "이메일은 필수 입력값입니다.")
        String email;

        @NotNull(message = "이름은 필수 입력값입니다.")
        String name;

        @NotNull(message = "전화번호는 필수 입력값입니다.")
        @Pattern(regexp = "010([2-9])\\d{7,8}", message = "전화번호는 10 ~ 11 자리의 숫자만 입력 가능합니다.")
        String phone;

        Integer gender;

        LocalDate birthDate;

        String profileImg;
    }
}
