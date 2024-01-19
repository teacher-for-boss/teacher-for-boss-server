package kr.co.teacherforboss.web.controller;

import jakarta.validation.Valid;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.AuthConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.service.AuthService.AuthCommandService;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.web.dto.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;

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
}
