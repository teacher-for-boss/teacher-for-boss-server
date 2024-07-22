package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.co.teacherforboss.validation.annotation.CheckSurvey;
import kr.co.teacherforboss.validation.annotation.CheckTrueOrFalse;
import lombok.Builder;
import lombok.Getter;

public class MemberRequestDTO {

    @Getter
    @Builder
    public static class SurveyDTO {

        @NotNull(message = "사전정보 1번이 없습니다.")
        @CheckSurvey(question = 1, message = "사전정보 1번이 잘못되었습니다.")
        int question1;

        @NotNull(message = "사전정보 2번이 없습니다.")
        @NotEmpty(message = "사전정보 2번은 하나 이상의 값을 가져야 합니다.")
        @CheckSurvey(question = 2, message = "사전정보 2번이 잘못되었습니다.")
        List<Integer> question2;

        @NotNull(message = "사전정보 3번이 없습니다.")
        @CheckSurvey(question = 3, message = "사전정보 3번이 잘못되었습니다.")
        int question3;

        String question4;
    }

    @Getter
    @Builder
    public static class EditBossProfileDTO {

        String profileImg;

        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{1,10}$", message = "닉네임은 한국어/영어/숫자로 최대 10자 입력 가능합니다.")
        String nickname;

    }

    @Getter
    @Builder
    public static class EditTeacherProfileDTO {

        String profileImg;

        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{1,10}$", message = "닉네임은 한국어/영어/숫자로 최대 10자 입력 가능합니다.")
        String nickname;

        @Pattern(regexp = "010([2-9])\\d{7,8}", message = "전화번호는 10 ~ 11 자리의 숫자만 입력 가능합니다.")
        String phone;

        boolean phoneOpen;

        @Email(message = "이메일 형식이 아닙니다.")
        @NotNull(message = "이메일이 없습니다.")
        String email;

        boolean emailOpen;

        @Size(max = 20, message = "분야는 최대 20자 이내로 입력 가능합니다.")
        String field;

        @Max(value = 99, message = "경력은 십의 자리 수 이내로 입력 가능합니다.")
        Integer career;

        @Size(max = 40, message = "한 줄 소개는 최대 40자 이내로 입력 가능합니다.")
        String introduction;

        @Size(max = 5)
        List<String> keywords;
    }
}
