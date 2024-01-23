package kr.co.teacherforboss.service.memberService;

import kr.co.teacherforboss.domain.MemberSurvey;
import kr.co.teacherforboss.web.dto.MemberRequestDTO;

public interface MemberCommandService {
    MemberSurvey saveSurvey(MemberRequestDTO.SurveyDTO request);
}
