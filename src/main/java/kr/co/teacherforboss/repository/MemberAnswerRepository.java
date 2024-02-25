package kr.co.teacherforboss.repository;

import java.util.List;
import kr.co.teacherforboss.domain.MemberAnswer;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberAnswerRepository extends JpaRepository<MemberAnswer, Long> {

    List<MemberAnswer> findAllByMemberExamIdAndStatus(Long memberExamId, Status status);

    @Query(value = "select ma from MemberAnswer ma where ma.memberExam = ?1 and ma.questionChoice.id <> ma.question.answer")
    List<MemberAnswer> findIncorrectAnswers(MemberExam memberExam, Status status);

}
