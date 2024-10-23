package kr.co.teacherforboss.web.dto;

import java.util.List;
import kr.co.teacherforboss.domain.vo.notificationVO.NotificationLinkData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class NotificationResponseDTO {

    @Getter
    @Builder
    public static class SettingsDTO {
        boolean serviceNotification;
        MarketingNotificationDTO marketingNotification;

        @Getter
        @Builder
        @AllArgsConstructor
        public static class MarketingNotificationDTO {
            boolean push;
            boolean email;
            boolean sms;
        }
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
            NotificationLinkData data;
            boolean read;
            String createdAt;
        }
    }
}
