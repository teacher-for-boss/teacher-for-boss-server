package kr.co.teacherforboss.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import kr.co.teacherforboss.aop.AwsSnsAspect;
import kr.co.teacherforboss.aop.NotificationAspect;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.config.AwsSnsConfig;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.NotificationTopic;
import kr.co.teacherforboss.domain.enums.NotificationType;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.service.snsService.SnsService;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SNS Controller", description = "SNS API 입니다.")
@RestController
@RequestMapping("/api/v1/sns")
@RequiredArgsConstructor
public class SnsController {

    private final AwsSnsAspect awsSnsAspect;
    private final NotificationAspect notificationAspect;
    private final SnsService snsService;
    private final AuthCommandService authCommandService;

    @PostMapping("/endpoints")
    public ApiResponse<String> create(@RequestParam("fcmToken") String fcmToken,
                                      @RequestParam("platform") String platform) {
        Member member = authCommandService.getMember();
        snsService.createEndpoint(member, List.of(new AuthRequestDTO.DeviceInfoDTO(fcmToken, platform)), NotificationTopic.ALL);
        snsService.createEndpoint(member, List.of(new AuthRequestDTO.DeviceInfoDTO(fcmToken, platform)), NotificationTopic.GENERAL);
        return ApiResponse.onSuccess("success");
    }

    @DeleteMapping("/endpoints")
    public ApiResponse<String> delete(@RequestParam("fcmToken") String fcmToken) {
        Member member = authCommandService.getMember();
        snsService.deleteEndpoint(member, List.of(fcmToken), NotificationTopic.ALL);
        snsService.deleteEndpoint(member, List.of(fcmToken), NotificationTopic.GENERAL);
        return ApiResponse.onSuccess("success");
    }

    @DeleteMapping("/endpoints/all")
    public ApiResponse<String> deleteAll() {
        Member member = authCommandService.getMember();
        awsSnsAspect.deregisterAll(null, member);
        return ApiResponse.onSuccess("success");
    }

    @PostMapping("/publish/all")
    public ApiResponse<String> publishAll(@RequestParam("notificationType") NotificationType notificationType) {
        switch (notificationType) {
            case HOME_NEW_HOT_TEACHERS:
                notificationAspect.sendNewHotTeachersNotification();
                break;
            default:
                break;
        }
        return ApiResponse.onSuccess("success");
    }

    // ex. targetLoginInfoIds=1&targetLoginInfoIds=2&targetLoginInfoIds=3
    @PostMapping("/publish")
    public ApiResponse<String> publishAll(@RequestParam("message") String message,
                                          @RequestParam("targetMemberId") List<Long> targetMemberIds) {
        List<Member> targetMembers = authCommandService.getMembers(targetMemberIds);
        snsService.publishMessage(message, targetMembers);
        return ApiResponse.onSuccess("success");
    }
}
