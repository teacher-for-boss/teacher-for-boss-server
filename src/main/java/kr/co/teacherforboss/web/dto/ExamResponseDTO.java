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
        int problemsCount;
        int correctChoicesCount;
        int incorrectChoicesCount;
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
            String name;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetExamTagsDTO {
        List<ExamTagInfo> examTagsList;

        @Builder
        @Getter
        @AllArgsConstructor
        public static class ExamTagInfo {
            Long examTagId;
            String name;
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
            Long id;
            String examTag;
            String title;
            String description;
            boolean isTaken;
            Boolean isPassed;
            Integer score;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetExamIncorrectChoicesResultDTO {
        List<ExamIncorrectProblem> examIncorrectProblemList;

        @Getter
        @AllArgsConstructor
        public static class ExamIncorrectProblem {
            Long problemId;
            int sequence;
            String name;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetProblemsDTO {
        List<ProblemInfo> problemList;

        @Getter
        @AllArgsConstructor
        public static class ProblemInfo {
            Long problemId;
            Integer sequence;
            String name;
            List<ProblemChoiceInfo> choiceList;

            @Getter
            @AllArgsConstructor
            public static class ProblemChoiceInfo {
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
        List<ProblemSolution> solutionList;

        @Getter
        @AllArgsConstructor
        public static class ProblemSolution {
            Long problemId;
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
