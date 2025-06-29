package kr.co.teacherforboss.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.vo.questionVO.QuestionExtraData;
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
        LocalDateTime createdAt;
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
        boolean liked;
        boolean bookmarked;
        Integer likeCount;
        Integer bookmarkCount;
        Integer commentCount;
        LocalDateTime createdAt;
        Boolean isMine;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditPostDTO {
        Long postId;
        LocalDateTime updatedAt;
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
            LocalDateTime selectedAt;
            LocalDateTime createdAt;
            MemberInfo memberInfo;
            List<String> imageUrlList;
            Boolean isMine;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCommentsDTO {
        boolean hasNext;
        List<CommentInfo> commentList;

        @Builder
        @Getter
        @AllArgsConstructor
        public static class CommentInfo {
            Long commentId;
            String content;
            int likeCount;
            int dislikeCount;
            boolean liked;
            boolean disliked;
            LocalDateTime createdAt;
            MemberInfo memberInfo;
            Boolean isMine;
            boolean isDeleted;
            List<CommentInfo> children;
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
        Role role;
        String level;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPostsDTO {
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
            Boolean liked;
            Boolean bookmarked;
            LocalDateTime createdAt;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TogglePostBookmarkDTO {
        Boolean bookmark;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TogglePostLikeDTO {
        Boolean like;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeletePostDTO {
        long postId;
        LocalDateTime deletedAt;
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
    public static class ToggleQuestionLikeDTO {
        Long questionId;
        boolean liked;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToggleAnswerLikeDTO {
        Long answerId;
        Boolean liked;
        Integer likedCount;
        Integer dislikedCount;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToggleCommentLikeDTO {
        Long commentId;
        Boolean liked;
        Integer likedCount;
        Integer dislikedCount;
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
    public static class ToggleQuestionBookmarkDTO {
        Long questionId;
        boolean bookmarked;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetQuestionDTO {
        String title;
        String content;
        QuestionExtraData extraData;
        String category;
        List<String> imageUrlList;
        List<String> hashtagList;
        MemberInfo memberInfo;
        boolean liked;
        boolean bookmarked;
        Integer likeCount;
        Integer bookmarkCount;
        Integer answerCount;
        LocalDateTime createdAt;
        Boolean isMine;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveCommentDTO {
        Long commentId;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectAnswerDTO {
        Long selectedAnswerId;
        LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
	public static class GetQuestionsDTO {
        boolean hasNext;
        List<GetQuestionsDTO.QuestionInfo> questionList;

        @Getter
        @AllArgsConstructor
        public static class QuestionInfo {
            Long questionId;
            String category;
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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteCommentDTO {
        Long commentId;
        LocalDateTime deletedAt;
    }

}
