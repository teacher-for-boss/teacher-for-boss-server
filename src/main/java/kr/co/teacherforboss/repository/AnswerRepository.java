package kr.co.teacherforboss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
	@Query(value = """
   			UPDATE answer a
			SET a.status = 'INACTIVE'
			WHERE a.question_id = :questionId
			""", nativeQuery = true)
	void softDeleteByQuestionId(@Param("questionId") Long questionId);
}
