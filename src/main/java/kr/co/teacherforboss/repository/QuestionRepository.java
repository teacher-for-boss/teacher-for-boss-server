package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Integer countByExamIdAndStatus(Long examId, Status status);
    Optional<Question> findByIdAndStatus(Long questionId, Status status);
}
