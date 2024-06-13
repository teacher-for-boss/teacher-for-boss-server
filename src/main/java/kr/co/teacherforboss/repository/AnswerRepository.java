package kr.co.teacherforboss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.enums.Status;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
	List<Answer> findAllByQuestionIdAndStatus(Long questionId, Status status);
}
