package kr.co.teacherforboss.web.dto;

import java.time.LocalDateTime;
import java.util.List;
import kr.co.teacherforboss.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MypageResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetQuestionInfosDTO {
        boolean hasNext;
        List<QuestionInfo> questionList;

        @Getter
        @AllArgsConstructor
        public static class QuestionInfo {
            Long questionId;
            String category;
            String title;
            String content;
            boolean solved;
            String selectedTeacher;
            LocalDateTime createdAt;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPostInfosDTO {
        boolean hasNext;
        List<PostInfo> postList;

        @Getter
        @AllArgsConstructor
        public static class PostInfo {
            Long postId;
            String title;
            String content;
            int bookmarkCount;
            int commentCount;
            int likeCount;
            boolean liked;
            boolean bookmarked;
            LocalDateTime createdAt;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetChipInfoDTO {
        Role memberRole;
        long answerCount;
        long questionCount;
        long bookmarkCount;
        int points;
        int questionTicketCount;
    }
}
