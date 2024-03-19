package kr.co.teacherforboss.repository;

import java.util.Optional;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberExamRepository extends JpaRepository<MemberExam, Long> {
    Optional<MemberExam> findByIdAndMemberAndStatus(Long memberExamId, Member member, Status status);
}
