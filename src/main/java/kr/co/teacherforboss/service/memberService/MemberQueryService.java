package kr.co.teacherforboss.service.memberService;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;

public interface MemberQueryService {
    MemberResponseDTO.GetMemberProfileDTO getMemberProfile();
    MemberResponseDTO.GetTeacherProfileDetailDTO getTeacherProfileDetail(Long memberId);
    MemberResponseDTO.GetRecentAnswersDTO getRecentAnswers();
}
