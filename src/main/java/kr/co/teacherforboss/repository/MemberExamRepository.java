package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.MemberExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberExamRepository extends JpaRepository<MemberExam, Long> {
    boolean existsByMemberIdAndExamId(Long memberId, Long examId);
}
