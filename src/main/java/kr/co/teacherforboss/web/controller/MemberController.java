package kr.co.teacherforboss.web.controller;

import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.MemberConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.service.memberService.MemberQueryService;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.co.teacherforboss.domain.MemberSurvey;
import kr.co.teacherforboss.service.memberService.MemberCommandService;
import kr.co.teacherforboss.web.dto.MemberRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;
    private final AuthCommandService authCommandService;

    @GetMapping("/profiles")
    public ApiResponse<MemberResponseDTO.GetMemberProfileDTO> getMemberProfile() {
        return ApiResponse.onSuccess(memberQueryService.getMemberProfile());
    }

    @GetMapping("/profiles/teacher/detail")
    public ApiResponse<MemberResponseDTO.GetTeacherProfileDetailDTO> getTeacherProfileDetail(@RequestParam(value = "memberId", required = false) Long memberId) {
        return ApiResponse.onSuccess(memberQueryService.getTeacherProfileDetail(memberId));
    }

    @PostMapping("/survey")
    public ApiResponse<MemberResponseDTO.SurveyResultDTO> saveSurvey(@RequestBody @Valid MemberRequestDTO.SurveyDTO request) {
        MemberSurvey memberSurvey = memberCommandService.saveSurvey(request);
        return ApiResponse.onSuccess(MemberConverter.toSurveyResultDTO(memberSurvey));
    }

    @PatchMapping("/profiles/boss")
    public ApiResponse<MemberResponseDTO.EditMemberProfileDTO> editBossProfile(@RequestBody @Valid MemberRequestDTO.EditBossProfileDTO request) {
        Member member = memberCommandService.editBossProfile(request);
        return ApiResponse.onSuccess(MemberConverter.toEditMemberProfileDTO(member));
    }

    @PatchMapping("/profiles/teacher")
    public ApiResponse<MemberResponseDTO.EditMemberProfileDTO> editTeacherProfile(@RequestBody @Valid MemberRequestDTO.EditTeacherProfileDTO request) {
        Member member = memberCommandService.editTeacherProfile(request);
        return ApiResponse.onSuccess(MemberConverter.toEditMemberProfileDTO(member));
    }

    @GetMapping("/accounts")
    public ApiResponse<MemberResponseDTO.GetMemberAccountInfoDTO> getAccountInfo() {
        Member member = authCommandService.getMember();
        return ApiResponse.onSuccess(MemberConverter.toGetMemberAccountInfoDTO(member));
    }
}
