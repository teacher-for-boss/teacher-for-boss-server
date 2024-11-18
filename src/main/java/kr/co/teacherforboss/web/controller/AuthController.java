package kr.co.teacherforboss.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.config.jwt.JwtTokenProvider;
import kr.co.teacherforboss.converter.AuthConverter;
import kr.co.teacherforboss.domain.BusinessAuth;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.PhoneAuth;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.service.authService.AuthQueryService;
import kr.co.teacherforboss.service.notificationService.NotificationCommandService;
import kr.co.teacherforboss.validation.annotation.CheckSocialType;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.web.dto.AuthResponseDTO;
import kr.co.teacherforboss.web.dto.NotificationRequestDTO;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;
    private final JwtTokenProvider jwtTokenProvider;
    private final NotificationCommandService notificationCommandService;

    @PostMapping("/signup")
    public ApiResponse<AuthResponseDTO.JoinResultDTO> join(@RequestBody @Valid AuthRequestDTO.JoinDTO request){
        Member member = authCommandService.joinMember(request);
        return ApiResponse.onSuccess(AuthConverter.toJoinResultDTO(member));
    }

    @PostMapping("/signup/notifications/settings")
    public ApiResponse<NotificationResponseDTO.SettingsDTO> updateSettings(
            @RequestHeader("Member-Id") Long memberId,
            @RequestBody @Valid NotificationRequestDTO.SettingsDTO request
    ) {
        return ApiResponse.onSuccess(notificationCommandService.updateSettings(memberId, request));
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

    @PostMapping("/teacher/business-number/check")
    public ApiResponse<AuthResponseDTO.CheckBusinessNumberResultDTO> checkBusinessNumber(@RequestBody @Valid AuthRequestDTO.CheckBusinessNumberDTO request) {
        BusinessAuth businessAuth = authCommandService.checkBusinessNumber(request);
        return ApiResponse.onSuccess(AuthConverter.toCheckBusinessNumberResultDTO(businessAuth));
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
        return ApiResponse.onSuccess(jwtTokenProvider.createTokenResponse(
                member.getId(), member.getEmail(), member.getName(), member.getRole()));
    }

    @PostMapping("/logout")
    public ApiResponse<AuthResponseDTO.LogoutResultDTO> logout(@NotNull @RequestHeader("Authorization") String accessToken,
                                                               @NotNull @RequestHeader("FCM-Token") String fcmToken) {
        String jwtToken = jwtTokenProvider.resolveTokenFromRequest(accessToken);
        Member member = authCommandService.logout(jwtToken, fcmToken);
        return ApiResponse.onSuccess(AuthConverter.toLogoutResultDTO(member.getEmail(), jwtToken));
    }

    @PostMapping("/reissue")
    public ApiResponse<AuthResponseDTO.TokenResponseDTO> reissueToken(@RequestHeader("RefreshToken") String refreshToken) {
        AuthResponseDTO.TokenResponseDTO tokenResponseDTO = jwtTokenProvider.recreateAccessToken(refreshToken);
        return ApiResponse.onSuccess(tokenResponseDTO);
    }

    @PatchMapping("/resetPassword")
    public ApiResponse<AuthResponseDTO.ResetPasswordResultDTO> resetPassword(@RequestBody @Valid AuthRequestDTO.ResetPasswordDTO request) {
        Member member = authCommandService.resetPassword(request);
        return ApiResponse.onSuccess(AuthConverter.toResetPasswordResultDTO(member));
    }

    @Validated
    @PostMapping("/login/social")
    public ApiResponse<AuthResponseDTO.TokenResponseDTO> socialLogin(@RequestBody @Valid AuthRequestDTO.SocialLoginDTO request,
                                                                     @RequestParam(name = "socialType") @CheckSocialType int socialType) {
        Member member = authCommandService.socialLogin(request, socialType);
        return ApiResponse.onSuccess(jwtTokenProvider.createTokenResponse(
                member.getId(), member.getEmail(), member.getName(), member.getRole()));
    }

    @PostMapping("/nickname/check")
    public ApiResponse<AuthResponseDTO.CheckNicknameResultDTO> checkNickname(@RequestBody @Valid AuthRequestDTO.CheckNicknameDTO request) {
        Boolean nicknameCheck = authQueryService.checkNickname(request);
        return ApiResponse.onSuccess(AuthConverter.toCheckNicknameDTO(nicknameCheck));
    }

    @DeleteMapping("/withdraw")
    public ApiResponse<AuthResponseDTO.WithdrawDTO> withdraw() {
        Member member = authCommandService.withdraw();
        return ApiResponse.onSuccess(AuthConverter.toWithdrawResultDTO(member));
    }

    @PatchMapping("/recover/for-test")
    public ApiResponse<AuthResponseDTO.RecoverDTO> recover(@RequestHeader("email") String email) {
        Member member = authCommandService.recover(email);
        return ApiResponse.onSuccess(AuthConverter.toRecoverDTO(member));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/signup/approve")
    public ApiResponse<AuthResponseDTO.CompleteTeacherSignupDTO> completeTeacherSignup(@RequestBody @Valid AuthRequestDTO.CompleteTeacherSignupDTO request) {
        Member member = authCommandService.completeTeacherSignup(request);
        return ApiResponse.onSuccess(AuthConverter.toCompleteTeacherSignupDTO(member));
    }
}
