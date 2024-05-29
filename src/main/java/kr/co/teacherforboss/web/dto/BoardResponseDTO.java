package kr.co.teacherforboss.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        Long memberId;
        String name;
        String profileImg;
    }
}
