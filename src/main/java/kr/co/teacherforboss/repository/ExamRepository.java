package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByIdAndStatus(Long examId, Status status);
}
