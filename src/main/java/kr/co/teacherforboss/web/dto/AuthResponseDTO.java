package kr.co.teacherforboss.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AuthResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinResultDTO{
        Long memberId;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendCodeMailResultDTO {
        Long emailAuthId;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckCodeMailResultDTO {
        boolean isChecked;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendCodePhoneResultDTO {
        Long phoneAuthId;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckResultDTO {
        boolean isChecked;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindPasswordResultDTO {
        Long memberId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponseDTO {
        String email;
        String name;
        String accessToken;
        String refreshToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogoutResultDTO {
        String email;
        String accessToken;
        LocalDateTime logoutAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPasswordResultDTO {
        Long memberId;
        boolean isChanged;
    }
  
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindEmailResultDTO {
        String email;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckNicknameResultDTO {
        boolean nicknameCheck;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckBusinessNumberResultDTO {
        Long businessAuthId;
        boolean isChecked;
    }
}
