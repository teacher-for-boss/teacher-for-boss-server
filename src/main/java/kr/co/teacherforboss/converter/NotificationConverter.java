package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.domain.NotificationSetting;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;

public class NotificationConverter {

    public static NotificationResponseDTO.SettingsDTO toSettingsDTO(NotificationSetting notificationSetting) {
        return NotificationResponseDTO.SettingsDTO.builder()
                .serviceNotification(notificationSetting.getService().isIdentifier())
                .marketingNotification(notificationSetting.getMarketing().isIdentifier())
                .build();
    }
}
