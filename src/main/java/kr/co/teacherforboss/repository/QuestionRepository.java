package kr.co.teacherforboss.repository;

import java.util.Optional;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByIdAndStatus(Long questionId, Status status);
    boolean existsByIdAndStatus(Long questionId, Status status);
}
