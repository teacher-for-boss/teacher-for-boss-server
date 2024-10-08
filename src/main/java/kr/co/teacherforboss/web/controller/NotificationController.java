package kr.co.teacherforboss.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.service.notificationService.NotificationCommandService;
import kr.co.teacherforboss.service.notificationService.NotificationQueryService;
import kr.co.teacherforboss.web.dto.NotificationRequestDTO;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification Controller", description = "Notification API 입니다.")
@RestController
@RequestMapping("/api/v1/notification")
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

}
