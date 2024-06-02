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
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPostDTO{
        String title;
        String content;
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
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        Long memberId;
        String name;
        String profileImg;
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
}
