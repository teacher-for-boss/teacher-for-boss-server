package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSurveyRepository extends JpaRepository<MemberSurvey, Long> {
    boolean existsByMember(Member member);
}
