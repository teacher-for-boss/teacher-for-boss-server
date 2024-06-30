package kr.co.teacherforboss.repository;

import java.util.List;
import java.util.Optional;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.Status;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
	Optional<Answer> findByIdAndMemberIdAndStatus(Long answerId, Long memberId, Status status);
	Optional<Answer> findByIdAndQuestionIdAndMemberIdAndStatus(Long answerId, Long questionId, Long memberId, Status status);
	Optional<Answer> findByIdAndQuestionIdAndStatus(Long answerId, Long questionId, Status status);
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = """
		UPDATE answer a
		SET a.status = 'INACTIVE'
		WHERE a.question_id = :questionId
	""", nativeQuery = true)
	void softDeleteAnswersByQuestionId(@Param(value = "questionId") Long questionId);
    List<Answer> findAllByQuestionIdAndStatusOrderByCreatedAt(Long questionId, Status status);

	Slice<Answer> findSliceByStatusOrderByCreatedAtDesc(Status status, Pageable pageable);
	@Query(value = """
		SELECT * FROM answer
		WHERE created_at < (SELECT created_at FROM answer WHERE id = :lastAnswerId)
			AND status = 'ACTIVE'
		ORDER BY created_at DESC
	""", nativeQuery = true)
	Slice<Answer> findSliceByIdLessThanAndStatusOrderByCreatedAtDesc(Long lastAnswerId, Pageable pageable);
	Optional<Answer> findByQuestionIdAndSelected(Long quesionId, BooleanType selected);
}
