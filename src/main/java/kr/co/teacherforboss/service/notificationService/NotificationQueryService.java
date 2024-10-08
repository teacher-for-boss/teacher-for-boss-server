package kr.co.teacherforboss.service.notificationService;

import kr.co.teacherforboss.web.dto.NotificationResponseDTO;

public interface NotificationQueryService {
    NotificationResponseDTO.SettingsDTO getSettings();
    NotificationResponseDTO.GetNotificationsDTO getNotifications(Long lastNotificationId, int size);
}
