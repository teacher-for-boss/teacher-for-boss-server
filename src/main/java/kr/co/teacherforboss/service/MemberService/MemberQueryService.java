package kr.co.teacherforboss.service.MemberService;

import kr.co.teacherforboss.domain.Member;


public interface MemberQueryService {
    Member viewMemberProfile(Long memberId);
}
