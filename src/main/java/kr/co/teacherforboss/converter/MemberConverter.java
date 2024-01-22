package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;

public class MemberConverter {

    public static MemberResponseDTO.ViewMemberProfileDTO toViewMemberProfileDTO(Member member){
        return MemberResponseDTO.ViewMemberProfileDTO.builder()
                .name(member.getName())
                .profileImg(member.getProfileImg())
                .build();
    }
}
