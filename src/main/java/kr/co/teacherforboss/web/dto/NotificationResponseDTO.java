package kr.co.teacherforboss.web.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class NotificationResponseDTO {

    @Getter
    @Builder
    public static class SettingsDTO {
        boolean serviceNotification;
        boolean marketingNotification;
    }

    @Getter
    @Builder
    public static class GetNotificationsDTO {
        boolean hasNext;
        List<NotificationInfo> notificationList;

        @Getter
        @Builder
        @AllArgsConstructor
        public static class NotificationInfo {
            Long notificationId;
            String title;
            String content;
            String type;
            boolean read;
            String createdAt;
        }
    }
}
