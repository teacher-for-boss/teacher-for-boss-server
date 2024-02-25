package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.MemberAnswer;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberAnswerRepository extends JpaRepository<MemberAnswer, Long> {

    List<MemberAnswer> findAllByMemberExamIdAndStatus(Long memberExamId, Status status);

    @Query(value = "select ma from MemberAnswer ma where ma.memberExam = ?1 and ma.questionChoice.choice != ma.question.answer")
    List<MemberAnswer> findIncorrectAnswers(MemberExam memberExam, Status status);

}
