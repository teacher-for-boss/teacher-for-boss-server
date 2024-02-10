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
    public static class TakeExamsDTO{

        @NotNull
        List<TakeExamsChoicesDTO> questionAnsList;

    }

    @Getter
    @Builder
    public static class TakeExamsChoicesDTO{
        @NotNull
        Long questionId;

        @NotNull
        String questionChoice;
    }
}
