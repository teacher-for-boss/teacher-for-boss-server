package kr.co.teacherforboss.web.controller;

import jakarta.validation.Valid;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.MemberConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberSurvey;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.service.memberService.MemberCommandService;
import kr.co.teacherforboss.service.memberService.MemberQueryService;
import kr.co.teacherforboss.web.dto.MemberRequestDTO;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/profiles/teacher/{memberId}")
    public ApiResponse<MemberResponseDTO.GetTeacherProfileDetailDTO> getTeacherProfileDetail(@PathVariable("memberId") Long memberId) {
        return ApiResponse.onSuccess(memberQueryService.getTeacherProfileDetail(memberId));
    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/profiles/teacher/{memberId}/recent-answers")
    public ApiResponse<MemberResponseDTO.GetRecentAnswersDTO> getRecentAnswers(@PathVariable("memberId") Long memberId) {
        return ApiResponse.onSuccess(memberQueryService.getRecentAnswers(memberId));
    }

    @PostMapping("/survey")
    public ApiResponse<MemberResponseDTO.SurveyResultDTO> saveSurvey(@RequestBody @Valid MemberRequestDTO.SurveyDTO request) {
        MemberSurvey memberSurvey = memberCommandService.saveSurvey(request);
        return ApiResponse.onSuccess(MemberConverter.toSurveyResultDTO(memberSurvey));
    }

    @PreAuthorize("hasRole('ROLE_BOSS')")
    @PatchMapping("/profiles/boss")
    public ApiResponse<MemberResponseDTO.EditMemberProfileDTO> editBossProfile(@RequestBody @Valid MemberRequestDTO.EditBossProfileDTO request) {
        Member member = memberCommandService.editBossProfile(request);
        return ApiResponse.onSuccess(MemberConverter.toEditMemberProfileDTO(member));
    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
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
