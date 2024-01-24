package kr.co.teacherforboss.web.dto;

import kr.co.teacherforboss.validation.annotation.CheckSurvey;
import lombok.Builder;
import lombok.Getter;

public class MemberRequestDTO {

    @Getter
    @Builder
    public static class SurveyDTO {

        @CheckSurvey(question = 1, message = "사전정보 1번이 잘못되었습니다.")
        int question1;

        @CheckSurvey(question = 2, message = "사전정보 2번이 잘못되었습니다.")
        int question2;

        @CheckSurvey(question = 3, message = "사전정보 3번이 잘못되었습니다.")
        int question3;

        String question4;
    }
}
