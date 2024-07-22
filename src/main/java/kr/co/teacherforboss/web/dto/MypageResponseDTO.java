package kr.co.teacherforboss.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class MypageResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GetMyQuestionsDTO {
        boolean hasNext;
        List<MypageResponseDTO.GetMyQuestionsDTO.QuestionInfo> questionList;

        @Getter
        @AllArgsConstructor
        public static class QuestionInfo {
            Long questionId;
            String title;
            String content;
            Boolean solved;
            String selectedTeacher;
            Integer bookmarkCount;
            Integer answerCount;
            Integer likeCount;
            Boolean liked;
            Boolean bookmarked;
            LocalDateTime createdAt;
        }
    }
    
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
            Long questionId;
            String category;
            String title;
            String content;
            boolean solved;
            String selectedTeacher;
            LocalDateTime createdAt;
        }
    }
}
