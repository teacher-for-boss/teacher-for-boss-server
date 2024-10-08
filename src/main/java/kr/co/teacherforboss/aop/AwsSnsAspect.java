package kr.co.teacherforboss.aop;

import kr.co.teacherforboss.config.jwt.JwtTokenProvider;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.service.snsService.SnsService;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.web.dto.AuthRequestDTO.DeviceInfoDTO;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AwsSnsAspect {

    private final SnsService snsService;

    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.authService.AuthCommandService.joinMember(..)) || " +
            "execution(* kr.co.teacherforboss.service.authService.AuthCommandService.socialLogin(..))",
            returning = "member")
    public void register(JoinPoint joinPoint, Member member) {
        System.out.println("Method Name: " + joinPoint.getSignature().getName());

        Object arg = joinPoint.getArgs()[0];
        DeviceInfoDTO deviceInfo = ((AuthRequestDTO.JoinCommonDTO) arg).getDeviceInfo();
        snsService.createEndpoint(member, deviceInfo);
    }

    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.authService.AuthCommandService.logout(..))", returning = "member")
    public void deregister(JoinPoint joinPoint, Member member) {
        System.out.println("Method Name: " + joinPoint.getSignature().getName());
        String fcmToken = ((String)joinPoint.getArgs()[1]);
        snsService.deleteEndpoint(member, fcmToken);
    }

    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.authService.AuthCommandService.withdraw(..))", returning = "member")
    public void deregisterAll(JoinPoint joinPoint, Member member) {
        System.out.println("Method Name: " + joinPoint.getSignature().getName());
        snsService.deleteEndpoint(member);
    }
}
