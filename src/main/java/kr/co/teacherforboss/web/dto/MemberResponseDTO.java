package kr.co.teacherforboss.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMemberProfileDTO {
        String name;
        String profileImg;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurveyResultDTO {
        Long surveyId;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfoDTO {
        Long memberId;
        String nickname;
        String profileImg;
        String level;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditMemberProfileDTO {
        String nickname;
        String profileImg;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMemberAccountInfoDTO {
        String loginType;
        String email;
        String phone;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetTeacherProfileDTO {
        String nickname;
        String profileImg;
        String introduction;
        String phone;
        String email;
        String field;
        Integer career;
        List<String> keywords;
        String level;
        boolean isMine;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetQuestionsDTO {
        boolean hasNext;
        List<BoardResponseDTO.GetQuestionsDTO.QuestionInfo> questionList;

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


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentAnswersDTO {
        List<RecentAnswerInfo> recentAnswerList;

        @Getter
        @AllArgsConstructor
        public static class RecentAnswerInfo {

        }
    }

}
