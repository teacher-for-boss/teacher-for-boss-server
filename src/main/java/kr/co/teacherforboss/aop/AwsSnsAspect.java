package kr.co.teacherforboss.aop;

import java.util.List;
import kr.co.teacherforboss.config.AwsSnsConfig;
import kr.co.teacherforboss.domain.DeviceToken;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.NotificationTopic;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.service.deviceTokenService.DeviceTokenQueryService;
import kr.co.teacherforboss.service.snsService.SnsService;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.web.dto.AuthRequestDTO.DeviceInfoDTO;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import kr.co.teacherforboss.web.dto.NotificationRequestDTO;

@Aspect
@Component
@RequiredArgsConstructor
public class AwsSnsAspect {

    private final SnsService snsService;
    private final AuthCommandService authCommandService;
    private final DeviceTokenQueryService deviceTokenQueryService;

    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.authService.AuthCommandService.socialLogin(..)) || " +
            "execution(* kr.co.teacherforboss.service.authService.AuthCommandService.login(..)) || " +
            "execution(* kr.co.teacherforboss.service.authService.AuthCommandService.joinMember(..))",
            returning = "member")
    public void registerAll(JoinPoint joinPoint, Member member) {
        System.out.println("Method Name: " + joinPoint.getSignature().getName());

        Object arg = joinPoint.getArgs()[0];
        DeviceInfoDTO deviceInfo;

        if (arg instanceof AuthRequestDTO.LoginDTO loginDTO) {
            deviceInfo = loginDTO.getDeviceInfo();
        } else if (arg instanceof AuthRequestDTO.SocialLoginDTO socialLoginDTO) {
            deviceInfo = socialLoginDTO.getDeviceInfo();
        } else if (arg instanceof AuthRequestDTO.JoinDTO joinDTO) {
            deviceInfo = joinDTO.getDeviceInfo();
        } else {
            return;
        }

        if (deviceInfo == null) return;

         snsService.createEndpoint(member, List.of(deviceInfo), NotificationTopic.ALL);
         snsService.createEndpoint(member, List.of(deviceInfo), NotificationTopic.GENERAL);
    }

    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.notificationService.NotificationCommandService.updateSettings(..)) && args(request)", returning = "settings")
    public void registerOrDeregisterGeneral(JoinPoint joinPoint, NotificationRequestDTO.SettingsDTO request, NotificationResponseDTO.SettingsDTO settings) {
        Member member = authCommandService.getMember();
        List<DeviceInfoDTO> deviceInfoDTOS = deviceTokenQueryService.getDeviceTokens(member.getId()).stream().map(DeviceToken::toDeviceInfoDTO).toList();

        if (settings.isServiceNotification()) {
            System.out.println("register general endpoint");
            snsService.createEndpoint(member, deviceInfoDTOS, NotificationTopic.GENERAL);
        }
        if (!settings.isServiceNotification()) {
            System.out.println("deregister general endpoint");
            snsService.deleteEndpoint(member, deviceInfoDTOS.stream().map(DeviceInfoDTO::getFcmToken).toList(), NotificationTopic.GENERAL);
        }
    }

    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.authService.AuthCommandService.logout(..))", returning = "member")
    public void deregister(JoinPoint joinPoint, Member member) {
        System.out.println("Method Name: " + joinPoint.getSignature().getName());
        String fcmToken = ((String)joinPoint.getArgs()[1]);

        if (fcmToken == null) return;

        snsService.deleteEndpoint(member, List.of(fcmToken), NotificationTopic.ALL);
        snsService.deleteEndpoint(member, List.of(fcmToken), NotificationTopic.GENERAL);
    }

    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.authService.AuthCommandService.withdraw(..))", returning = "member")
    public void deregisterAll(JoinPoint joinPoint, Member member) {
        System.out.println("deregister all endpoints");
        snsService.deleteEndpoint(member);
    }
}
