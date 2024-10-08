package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.domain.Notification;
import kr.co.teacherforboss.domain.NotificationSetting;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;
import org.springframework.data.domain.Slice;

public class NotificationConverter {

    public static NotificationResponseDTO.SettingsDTO toSettingsDTO(NotificationSetting notificationSetting) {
        return NotificationResponseDTO.SettingsDTO.builder()
                .serviceNotification(notificationSetting.getService().isIdentifier())
                .marketingNotification(notificationSetting.getMarketing().isIdentifier())
                .build();
    }

    public static NotificationResponseDTO.GetNotificationsDTO toGetNotificationsDTO(Slice<Notification> notifications) {
        return NotificationResponseDTO.GetNotificationsDTO.builder()
                .hasNext(notifications.hasNext())
                .notificationList(notifications.getContent().stream()
                        .map(notification -> NotificationResponseDTO.GetNotificationsDTO.NotificationInfo.builder()
                                .notificationId(notification.getId())
                                .title(notification.getTitle())
                                .content(notification.getContent())
                                .type(notification.getType().name())
                                .read(notification.getIsRead().isIdentifier())
                                .createdAt(notification.getCreatedAt().toString())
                                .build())
                        .toList())
                .build();
    }
}
