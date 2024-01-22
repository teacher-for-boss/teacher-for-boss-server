package kr.co.teacherforboss.web.controller;

import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.MemberConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.service.MemberService.MemberQueryService;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberQueryService memberQueryService;

    @GetMapping("/profile")
    public ApiResponse<MemberResponseDTO.ViewMemberProfileDTO> viewMemberProfile() {
        Member member = memberQueryService.viewMemberProfile(1L); // TODO: 토큰 검증 메서드 추가
        return ApiResponse.onSuccess(MemberConverter.toViewMemberProfileDTO(member));
    }
}
