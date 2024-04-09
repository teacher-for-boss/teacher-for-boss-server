package kr.co.teacherforboss.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.QueryHint;
import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByExamCategoryIdAndStatus(Long examCategoryId, Status status);
    List<Exam> findByExamCategoryIdAndTagIdAndStatus(Long examCategoryId, Long tagId, Status status);
    Optional<Exam> findByIdAndStatus(Long examId, Status status);
    boolean existsByIdAndStatus(Long id, Status status);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query(value = "SELECT e.* " +
            "FROM exam e " +
            "WHERE EXISTS ( " +
            "    SELECT 1 " +
            "    FROM member_exam me " +
            "    WHERE e.id = me.exam_id " +
            "      AND me.member_id = :memberId " +
            "      AND me.status = 'ACTIVE' " +
            ");", nativeQuery = true)
    List<Exam> findAllTakenExamByMemberId(@Param("memberId") Long memberId);
}
