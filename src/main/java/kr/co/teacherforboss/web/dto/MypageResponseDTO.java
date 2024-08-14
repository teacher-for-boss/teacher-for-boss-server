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
        int commentCount;
        int bookmarkCount;
        int point;
        int questionTicketCount;
    }
}
