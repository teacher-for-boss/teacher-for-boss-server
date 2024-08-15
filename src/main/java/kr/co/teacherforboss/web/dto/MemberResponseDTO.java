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
        String nickname;
        String profileImg;
        String role;
        TeacherInfo teacherInfo;

        @Getter
        @Builder
        public static class TeacherInfo {
            String level;
            int leftAnswerCount;
        }
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
    public static class GetTeacherProfileDetailDTO {
        String nickname;
        String profileImg;
        String introduction;
        String phone;
        boolean phoneOpen;
        String email;
        boolean emailOpen;
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
    public static class GetRecentAnswersDTO {
        List<RecentAnswerInfo> recentAnswerList;

        @Getter
        @AllArgsConstructor
        public static class RecentAnswerInfo {
            Long questionId;
            String questionTitle;
            String answer;
            int answerLikeCount;
            LocalDateTime answeredAt;
        }
    }
}
