package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
