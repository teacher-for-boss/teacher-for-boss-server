package kr.co.teacherforboss.service.notificationService;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.web.dto.NotificationRequestDTO;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;

public interface NotificationCommandService {
    NotificationResponseDTO.SettingsDTO updateSettings(NotificationRequestDTO.SettingsDTO request);
}
