package kr.co.teacherforboss.service.memberService;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;


public interface MemberQueryService {
    Member getMemberProfile();
    MemberResponseDTO.GetTeacherProfileDTO getTeacherProfile();
    MemberResponseDTO.GetRecentAnswersDTO getRecentAnswers();
}
