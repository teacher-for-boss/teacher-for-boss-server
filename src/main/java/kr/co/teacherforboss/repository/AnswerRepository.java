package kr.co.teacherforboss.repository;

import java.util.List;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllByQuestionIdAndStatusOrderByCreatedAt(Long questionId, Status status);
}
