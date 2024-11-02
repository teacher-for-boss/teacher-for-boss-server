package kr.co.teacherforboss.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.service.notificationService.NotificationCommandService;
import kr.co.teacherforboss.service.notificationService.NotificationQueryService;
import kr.co.teacherforboss.web.dto.NotificationRequestDTO;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification Controller", description = "Notification API 입니다.")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    @GetMapping("/settings")
    public ApiResponse<NotificationResponseDTO.SettingsDTO> getSettings() {
        return ApiResponse.onSuccess(notificationQueryService.getSettings());
    }

    @PostMapping("/settings")
    public ApiResponse<NotificationResponseDTO.SettingsDTO> updateSettings(
            @RequestBody @Valid NotificationRequestDTO.SettingsDTO request
    ) {
        return ApiResponse.onSuccess(notificationCommandService.updateSettings(request));
    }

    @GetMapping
    public ApiResponse<NotificationResponseDTO.GetNotificationsDTO> getNotifications(
            @RequestParam(defaultValue = "0") Long lastNotificationId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.onSuccess(notificationQueryService.getNotifications(lastNotificationId, size));
    }

    @PostMapping("/{notificationId}/read")
    public ApiResponse<Void> readNotification(
            @PathVariable Long notificationId
    ) {
        notificationCommandService.readNotifications(List.of(notificationId));
        return ApiResponse.onSuccess();
    }

}
