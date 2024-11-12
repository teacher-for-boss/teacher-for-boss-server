package kr.co.teacherforboss.service.notificationService;

import java.util.List;
import kr.co.teacherforboss.web.dto.NotificationRequestDTO;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;

public interface NotificationCommandService {
    NotificationResponseDTO.SettingsDTO updateSettings(NotificationRequestDTO.SettingsDTO request);
    NotificationResponseDTO.SettingsDTO updateSettings(Long memberId, NotificationRequestDTO.SettingsDTO request);
    void readNotifications(List<Long> notificationIds);
}
