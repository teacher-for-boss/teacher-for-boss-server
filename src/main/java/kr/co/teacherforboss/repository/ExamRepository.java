package kr.co.teacherforboss.repository;

import java.util.List;
import java.util.Optional;
import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByIdAndStatus(Long examId, Status status);
    boolean existsByIdAndStatus(Long id, Status status);

    @Query(value = "select distinct e.* "
            + "from exam e, member_exam me "
            + "where e.id = me.exam_id "
            + "and me.member_id = :memberId "
            + "and me.status = 'ACTIVE'", nativeQuery = true)
    List<Exam> findAllTakenExamByMemberId(@Param("memberId") Long memberId);
}
