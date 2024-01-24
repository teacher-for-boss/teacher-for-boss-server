package kr.co.teacherforboss.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.config.jwt.JwtTokenProvider;
import kr.co.teacherforboss.config.jwt.PrincipalDetails;
import kr.co.teacherforboss.converter.AuthConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.PhoneAuth;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.validation.annotation.ExistPrincipalDetails;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.web.dto.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
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
    public ApiResponse<AuthResponseDTO.CheckResultDTO> checkCodeMail(@RequestBody @Valid AuthRequestDTO.CheckCodeMailDTO request) {
        boolean isChecked = authCommandService.checkCodeMail(request);
        return ApiResponse.onSuccess(AuthConverter.toCheckResultDTO(isChecked));
    }

    @PostMapping("/phone")
    public ApiResponse<AuthResponseDTO.SendCodePhoneResultDTO> sendCodePhone(@RequestBody @Valid AuthRequestDTO.SendCodePhoneDTO request) {
        PhoneAuth phoneAuth = authCommandService.sendCodePhone(request);
        return ApiResponse.onSuccess(AuthConverter.toSendCodePhoneResultDTO(phoneAuth));
    }

    @PostMapping("/phone/check")
    public ApiResponse<AuthResponseDTO.CheckResultDTO> checkCodePhone(@RequestBody @Valid AuthRequestDTO.CheckCodePhoneDTO request) {
        boolean isChecked = authCommandService.checkCodePhone(request);
        return ApiResponse.onSuccess(AuthConverter.toCheckResultDTO(isChecked));
    }

    @PostMapping("/find/password")
    public ApiResponse<AuthResponseDTO.FindPasswordResultDTO> findEmail(@RequestBody @Valid AuthRequestDTO.FindPasswordDTO request) {
        Member member = authCommandService.findPassword(request);
        return ApiResponse.onSuccess(AuthConverter.toFindPasswordResultDTO(member));
    }
  
    @PostMapping("/find/email")
    public ApiResponse<AuthResponseDTO.FindEmailResultDTO> findEmail(@RequestBody @Valid AuthRequestDTO.FindEmailDTO request) {
        Member memberEmail = authCommandService.findEmail(request);
        return ApiResponse.onSuccess(AuthConverter.toFindEmailResultDTO(memberEmail));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponseDTO.TokenResponseDTO> login(@RequestBody @Valid AuthRequestDTO.LoginDTO request) {
        Member member = authCommandService.login(request);
        AuthResponseDTO.TokenResponseDTO tokenResponseDTO = jwtTokenProvider.createTokenResponse(member.getEmail(), member.getRole());
        return ApiResponse.onSuccess(tokenResponseDTO);
    }

    @PostMapping("/logout")
    public ApiResponse<AuthResponseDTO.LogoutResultDTO> logout(@NotNull @RequestHeader("Authorization") String accessToken,
                                                               @ExistPrincipalDetails @AuthenticationPrincipal PrincipalDetails principalDetails) {
        AuthResponseDTO.LogoutResultDTO logoutResultDTO = authCommandService.logout(accessToken, principalDetails.getEmail());
        return ApiResponse.onSuccess(AuthConverter.toLogoutResultDTO(logoutResultDTO.getEmail(), accessToken));
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
