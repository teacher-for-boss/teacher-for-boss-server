package kr.co.teacherforboss.config.jwt.exception;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SecurityExceptionController {

    @Operation(summary = "ACCESS_DENIED 오류 발생 시", description = "로그인은 되어 있으나 접근 권한이 없는 경우")
    @GetMapping("/exception/auth-denied")
    public void accessDenied() {
        log.info("ACCESS_DENIED - SecurityController");
        throw new AuthHandler(ErrorStatus.ACCESS_DENIED);
    }

    @Operation(summary = "LOGIN_REQUIRED 오류 발생 시", description = "로그인 되지 않은 경우")
    @GetMapping("/exception/unauthorized")
    public void unauthorized() {
        log.info("LOGIN_REQUIRED - SecurityController");
        throw new AuthHandler(ErrorStatus.LOGIN_REQUIRED);
    }
}