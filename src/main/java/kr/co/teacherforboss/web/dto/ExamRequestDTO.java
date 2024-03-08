package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


public class ExamRequestDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TakeExamDTO{

        @NotNull(message = "문항 정답 리스트는 필수 입력값입니다.")
        List<TakeExamChoiceDTO> questionAnsList;

        @NotNull(message = "남은 시간은 필수 입력값입니다.")
        Long leftTime;

    }

    @Getter
    @Builder
    public static class TakeExamChoiceDTO{
        @NotNull(message = "문항 식별자는 필수 입력값입니다.")
        Long questionId;

        @NotNull(message = "문항 선지 식별자는 필수 입력값입니다.")
        Long questionChoiceId;
    }
}
