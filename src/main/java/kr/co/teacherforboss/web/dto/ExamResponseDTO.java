package kr.co.teacherforboss.web.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ExamResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TakeExamsDTO{
        Long memberExamId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetExamResultDTO{
        int score;
        int questionsNum;
        int correctAnsNum;
        int incorrectAnsNum;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetExamCategoriesDTO {
        List<ExamCategoryInfo> examCategoryList;

        @Getter
        @AllArgsConstructor
        public static class ExamCategoryInfo {
            Long examCategoryId;
            String categoryName;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetExamAnsNotesResultDTO {
        List<ExamWrongQuestion> examWrongQuestionList;

        @Getter
        @AllArgsConstructor
        public static class ExamWrongQuestion {
            int questionSequence;
            String questionName;
        }
    }
}
