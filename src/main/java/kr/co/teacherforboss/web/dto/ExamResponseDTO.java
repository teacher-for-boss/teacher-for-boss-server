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
    public static class TakeExamDTO {
        Long memberExamId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetExamResultDTO{
        long memberExamId;
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
    public static class GetTagsDTO {
        private List<TagInfo> tagsList;

        @Builder
        @Getter
        @AllArgsConstructor
        public static class TagInfo {
            private Long tagId;
            private String tagName;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetExamsDTO {
        List<ExamInfo> examList;

        @Builder
        @Getter
        @AllArgsConstructor
        public static class ExamInfo {
            private Long examId;
            private String examName;
            private String examDescription;
            private String tagName;
            private boolean examYN;
            private Boolean examPassYN;
            private Integer examScore;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetExamIncorrectAnswersResultDTO {
        List<ExamIncorrectQuestion> examIncorrectQuestionList;

        @Getter
        @AllArgsConstructor
        public static class ExamIncorrectQuestion {
            Long questionId;
            int questionSequence;
            String questionName;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetQuestionsDTO {
        List<QuestionInfo> questionList;

        @Getter
        @AllArgsConstructor
        public static class QuestionInfo {
            Long questionId;
            Integer questionSequence;
            String questionName;
            List<QuestionChoiceInfo> choiceList;

            @Getter
            @AllArgsConstructor
            public static class QuestionChoiceInfo {
                Long choiceId;
                String choiceName;
            }
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetSolutionsDTO {
        List<QuestionSolution> solutionList;

        @Getter
        @AllArgsConstructor
        public static class QuestionSolution {
            Long questionId;
            String solution;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetExamRankInfoDTO {
        List<ExamRankInfo> examRankList;

        @Getter
        @Builder
        @AllArgsConstructor
        public static class ExamRankInfo {
            Long rank;
            Long memberId;
            String name;
            String profileImg;
            int score;
            boolean isMine;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAverageDTO{
        int averageScore;
        int userScore;
    }
  
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor  
    public static class GetTakenExamCountDTO {
        int takenExamsCount;
    }
}
