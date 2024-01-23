package kr.co.teacherforboss.web.controller;

import jakarta.validation.Valid;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.MemberConverter;
import kr.co.teacherforboss.domain.MemberSurvey;
import kr.co.teacherforboss.service.memberService.MemberCommandService;
import kr.co.teacherforboss.web.dto.MemberRequestDTO;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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

    private final MemberCommandService memberCommandService;

    @PostMapping("/survey")
    public ApiResponse<MemberResponseDTO.SurveyResultDTO> saveSurvey(@RequestBody @Valid MemberRequestDTO.SurveyDTO request) {
        MemberSurvey memberSurvey = memberCommandService.saveSurvey(request);
        return ApiResponse.onSuccess(MemberConverter.toSurveyResultDTO(memberSurvey));
    }
}
