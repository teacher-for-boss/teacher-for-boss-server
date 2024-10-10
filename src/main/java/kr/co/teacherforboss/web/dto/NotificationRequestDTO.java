package kr.co.teacherforboss.web.dto;

import kr.co.teacherforboss.web.dto.NotificationResponseDTO.SettingsDTO;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO.SettingsDTO.MarketingNotificationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NotificationRequestDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SettingsDTO {
        boolean serviceNotification;
        NotificationResponseDTO.SettingsDTO.MarketingNotificationDTO marketingNotification;

        @Getter
        @Builder
        @AllArgsConstructor
        public static class MarketingNotificationDTO {
            boolean push;
            boolean email;
            boolean sms;
        }
    }
}
