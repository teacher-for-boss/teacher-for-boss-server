package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.MemberAnswer;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberAnswerRepository extends JpaRepository<MemberAnswer, Long> {

    List<MemberAnswer> findAllByMemberExamIdAndStatus(Long memberExamId, Status status);
}
