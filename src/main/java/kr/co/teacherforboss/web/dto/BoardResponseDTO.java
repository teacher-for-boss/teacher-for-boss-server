package kr.co.teacherforboss.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.teacherforboss.domain.enums.BooleanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavePostDTO {
        Long postId;
        LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPostDTO{
        String title;
        String content;
        List<String> imageUrlList;
        List<String> hashtagList;
        MemberInfo memberInfo;
        String liked;
        String bookmarked;
        Integer likeCount;
        Integer bookmarkCount;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveQuestionDTO {
        Long questionId;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditQuestionDTO {
        Long questionId;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveAnswerDTO {
        Long answerId;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditAnswerDTO {
        Long answerId;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAnswersDTO {
        boolean hasNext;
        List<AnswerInfo> answerList;

        @Getter
        @Builder
        @AllArgsConstructor
        public static class AnswerInfo {
            Long answerId;
            String content;
            int likeCount;
            int dislikeCount;
            boolean liked;
            boolean disliked;
            boolean selected;
            LocalDateTime createdAt;
            MemberInfo memberInfo;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        Long memberId;
        String name;
        String profileImg;
        String level;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPostListDTO {
        boolean hasNext;
        List<PostInfo> postList;

        @Getter
        @AllArgsConstructor
        public static class PostInfo {
            Long postId;
            String title;
            String content;
            Integer bookmarkCount;
            Integer commentCount;
            Integer likeCount;
            Boolean like;
            Boolean bookmark;
            LocalDateTime createdAt;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavePostBookmarkDTO {
        Boolean bookmark;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavePostLikeDTO {
        Boolean like;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteQuestionDTO {
        Long questionId;
        LocalDateTime deletedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeQuestionDTO {
        Long questionId;
        BooleanType liked;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteAnswerDTO {
        Long answerId;
        LocalDateTime deletedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkQuestionDTO {
        Long questionId;
        BooleanType bookmarked;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetQuestionDTO {
        String title;
        String content;
        String category;
        List<String> imageUrlList;
        List<String> hashtagList;
        MemberInfo memberInfo;
        BooleanType liked;
        BooleanType bookmarked;
        Integer likeCount;
        Integer bookmarkCount;
        LocalDateTime createdAt;
    }
}
