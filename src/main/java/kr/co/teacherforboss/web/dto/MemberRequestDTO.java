package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import kr.co.teacherforboss.validation.annotation.CheckSurvey;
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
}
