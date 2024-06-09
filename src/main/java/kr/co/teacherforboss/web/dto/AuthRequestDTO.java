package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import kr.co.teacherforboss.validation.annotation.CheckPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.co.teacherforboss.validation.annotation.CheckTrueOrFalse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.List;

public class AuthRequestDTO {
    @Getter
    public static abstract class JoinCommonDTO{
//        @CheckRole
//        @NotNull(message = "보스(1)/티쳐(2) 중 선택해주세요.")
        Integer role;

        @Email(message = "이메일 형식이 아닙니다.")
        @NotNull(message = "이메일이 없습니다.")
        String email;

//        @NotBlank(message = "이름이 없습니다.")
        String name;

//        @NotNull(message = "닉네임을 입력해주세요.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{1,10}$", message = "닉네임은 한국어/영어/숫자로 최대 10자 입력 가능합니다.")
        String nickname;

//        @NotNull(message = "전화번호가 없습니다.")
        @Pattern(regexp = "010([2-9])\\d{7,8}", message = "전화번호는 10 ~ 11 자리의 숫자만 입력 가능합니다.")
        String phone;

        Integer gender;

        LocalDate birthDate;

//        @NotNull(message = "프로필 이미지를 선택해주세요.")
        String profileImg;

        @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$", message = "사업자 등록 번호는 최대 10자 이내로 입력 가능합니다.")
        String businessNumber;

        @Size(max = 20, message = "대표자명은 최대 20자 이내로 입력 가능합니다.")
        String representative;

        LocalDate openDate;

        @Size(max = 20, message = "분야는 최대 20자 이내로 입력 가능합니다.")
        String field;

        @Max(value = 99, message = "경력은 십의 자리 수 이내로 입력 가능합니다.")
        Integer career;

        @Size(max = 40, message = "한 줄 소개는 최대 40자 이내로 입력 가능합니다.")
        String introduction;

        @Size(max = 5)
        List<String> keywords;

        String bank;

        String accountNumber;

        String accountHolder;
    }

    @Getter
    @Builder
    public static class JoinDTO extends JoinCommonDTO{
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

        @NotNull(message = "Email 수신 동의 여부를 입력해주세요.")
        @CheckTrueOrFalse
        String agreementEmail;

        @NotNull(message = "위치 이용 동의 여부를 입력해주세요.")
        @CheckTrueOrFalse
        String agreementLocation;
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
        @Pattern(regexp = "\\d{5}", message = "인증 코드는 4자리의 숫자로 이루어져 있어야 합니다.")
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
    public static class SocialLoginDTO extends JoinCommonDTO{

    }

    @Getter
    @Jacksonized
    @Builder
    public static class CheckNicknameDTO {
        @NotNull(message = "닉네임을 입력해주세요.")
        String nickname;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckBusinessNumberDTO {

        @NotBlank(message = "사업자등록번호가 없습니다.")
        @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$", message = "사업자등록번호는 하이픈('-')을 포함한 10글자로 입력해주세요.")
        String businessNumber;

        @NotBlank(message = "대표자명이 없습니다.")
        String representative;

        @NotNull(message = "개업연월일이 없습니다.")
        LocalDate openDate;
    }
}
