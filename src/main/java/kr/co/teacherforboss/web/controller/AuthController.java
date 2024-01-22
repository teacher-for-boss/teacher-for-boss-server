package kr.co.teacherforboss.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.config.jwt.JwtTokenProvider;
import kr.co.teacherforboss.converter.AuthConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.web.dto.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ApiResponse<AuthResponseDTO.JoinResultDTO> join(@RequestBody @Valid AuthRequestDTO.JoinDTO request){
        Member member = authCommandService.joinMember(request);
        return ApiResponse.onSuccess(AuthConverter.toJoinResultDTO(member));
    }

    @PostMapping("/email")
    public ApiResponse<AuthResponseDTO.SendCodeMailResultDTO> sendCodeMail(@RequestBody @Valid AuthRequestDTO.SendCodeMailDTO request) {
        EmailAuth emailAuth = authCommandService.sendCodeMail(request);
        return ApiResponse.onSuccess(AuthConverter.toSendCodeMailResultDTO(emailAuth));
    }

    @PostMapping("/email/check")
    public ApiResponse<AuthResponseDTO.CheckCodeMailResultDTO> checkCodeMail(@RequestBody @Valid AuthRequestDTO.CheckCodeMailDTO request) {
        boolean isChecked = authCommandService.checkCodeMail(request);
        return ApiResponse.onSuccess(AuthConverter.toCheckCodeMailResultDTO(isChecked));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponseDTO.TokenResponseDTO> login(@RequestBody @Valid AuthRequestDTO.LoginDTO request) {
        Member member = authCommandService.login(request);
        AuthResponseDTO.TokenResponseDTO tokenResponseDTO = jwtTokenProvider.createTokenResponse(member.getEmail(), member.getRole());
        return ApiResponse.onSuccess(tokenResponseDTO);
    }

    @PostMapping("/logout")
    public ApiResponse<AuthResponseDTO.LogoutResultDTO> logout(HttpServletRequest request) {
        AuthResponseDTO.LogoutResultDTO logoutResultDTO = authCommandService.logout(request);
        return ApiResponse.onSuccess(logoutResultDTO);
    }

    @PostMapping("/reissue")
    public ApiResponse<AuthResponseDTO.TokenResponseDTO> reissueToken(@RequestHeader("RefreshToken") String refreshToken) {
        AuthResponseDTO.TokenResponseDTO tokenResponseDTO = jwtTokenProvider.recreateAccessToken(refreshToken);
        return ApiResponse.onSuccess(tokenResponseDTO);
    }

    @PatchMapping("/resetPassword")
    public ApiResponse<AuthResponseDTO.ResetPasswordResultDTO> resetPassword(@RequestBody @Valid AuthRequestDTO.resetPasswordDTO request) {
        Member member = authCommandService.resetPassword(request);
        return ApiResponse.onSuccess(AuthConverter.toResetPasswordResultDTO(member));
    }
}
