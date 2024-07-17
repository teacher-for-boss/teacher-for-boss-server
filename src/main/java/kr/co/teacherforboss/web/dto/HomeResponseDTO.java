package kr.co.teacherforboss.web.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class HomeResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetHotPostsDTO {
        List<HotPostInfo> hotPostList;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class HotPostInfo {
            Long postId;
            String title;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetHotQuestionsDTO {
        List<HotQuestionInfo> hotQuestionList;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class HotQuestionInfo {
            Long questionId;
            String category;
            String title;
            String content;
            int answerCount;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetHotTeachersDTO {
        List<HotTeacherInfo> hotTeacherList;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class HotTeacherInfo {
            Long memberId;
            String nickname;
            String imageUrl;
            String field;
            int career;
            List<String> keywords;
        }
    }
}
