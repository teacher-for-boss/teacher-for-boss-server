package kr.co.teacherforboss.web.controller;

import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.MemberConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.service.memberService.MemberQueryService;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import kr.co.teacherforboss.domain.MemberSurvey;
import kr.co.teacherforboss.service.memberService.MemberCommandService;
import kr.co.teacherforboss.web.dto.MemberRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/profile")
    public ApiResponse<MemberResponseDTO.GetMemberProfileDTO> getMemberProfile() {
        Member member = memberQueryService.getMemberProfile();
        return ApiResponse.onSuccess(MemberConverter.toGetMemberProfileDTO(member));
    }

    @PostMapping("/survey")
    public ApiResponse<MemberResponseDTO.SurveyResultDTO> saveSurvey(@RequestBody @Valid MemberRequestDTO.SurveyDTO request) {
        MemberSurvey memberSurvey = memberCommandService.saveSurvey(request);
        return ApiResponse.onSuccess(MemberConverter.toSurveyResultDTO(memberSurvey));
    }
}
