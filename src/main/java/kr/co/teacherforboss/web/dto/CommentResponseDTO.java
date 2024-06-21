package kr.co.teacherforboss.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveCommentDTO {
        Long postId;
        Long parentId;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveCommentLikeDTO {
        Boolean liked;
        LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCommentListDTO {
        Integer totalCount;
        List<CommentInfo> commentList;

        @Getter
        @AllArgsConstructor
        public static class CommentInfo {
            Long commentId;
            String content;
            Integer likeCount;
            Integer dislikeCount;
            LocalDateTime createdAt;
            MemberResponseDTO.MemberInfoDTO memberInfo;
            List<CommentInfo> children;
        }
    }
}
