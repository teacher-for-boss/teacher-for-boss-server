package kr.co.teacherforboss.web.dto;

import lombok.Builder;
import lombok.Getter;

public class NotificationResponseDTO {

    @Getter
    @Builder
    public static class SettingsDTO {
        boolean serviceNotification;
        boolean marketingNotification;
    }
}
