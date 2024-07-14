package kr.co.teacherforboss.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class MypageResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAnsweredQuestionsDTO {
        boolean hasNext;
        List<AnsweredQuestion> answeredQuestionList;

        @Getter
        @AllArgsConstructor
        public static class AnsweredQuestion {
            String title;
            String content;
            LocalDateTime createdAt;
            String category;
            boolean solved;
            Long questionId;
        }
    }
}
