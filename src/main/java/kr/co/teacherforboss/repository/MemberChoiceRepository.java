package kr.co.teacherforboss.repository;

import java.util.List;
import kr.co.teacherforboss.domain.MemberChoice;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberChoiceRepository extends JpaRepository<MemberChoice, Long> {

    List<MemberChoice> findAllByMemberExamIdAndStatus(Long memberExamId, Status status);

    @Query(value = "select ma from MemberChoice ma where ma.memberExam = ?1 and ma.problemChoice.id <> ma.problem.answer")
    List<MemberChoice> findIncorrectChoices(MemberExam memberExam, Status status);

}
