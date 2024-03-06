package kr.co.teacherforboss.repository;

import java.util.List;
import java.util.Optional;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Integer countByExamIdAndStatus(Long examId, Status status);
    Optional<Question> findByIdAndStatus(Long questionId, Status status);
    List<Question> findAllByExamIdAndStatus(Long examId, Status status);
    List<Question> findByIdInAndStatus(List<Long> idCollect, Status status);
}
