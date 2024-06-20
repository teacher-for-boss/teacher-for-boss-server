package kr.co.teacherforboss.repository;

import java.util.Optional;

import java.util.List;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.enums.Status;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
	Optional<Answer> findByIdAndMemberIdAndStatus(Long answerId, Long memberId, Status status);
	Optional<Answer> findByIdAndQuestionIdAndMemberIdAndStatus(Long answerId, Long questionId, Long memberId, Status status);
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = """
		UPDATE answer a
		SET a.status = 'INACTIVE'
		WHERE a.question_id = :questionId
	""", nativeQuery = true)
	void softDeleteAnswersByQuestionId(@Param(value = "questionId") Long questionId);
    List<Answer> findAllByQuestionIdAndStatusOrderByCreatedAt(Long questionId, Status status);
}
