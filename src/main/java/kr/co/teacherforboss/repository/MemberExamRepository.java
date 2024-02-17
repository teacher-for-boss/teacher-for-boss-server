package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberExamRepository extends JpaRepository<MemberExam, Long> {
    boolean existsByMemberIdAndExamId(Long memberId, Long examId);

    Optional<MemberExam> findByMemberIdAndExamIdAndStatus(Long memberId, Long examId, Status status);
}
