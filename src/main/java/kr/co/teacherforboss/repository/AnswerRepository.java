package kr.co.teacherforboss.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

	Optional<Answer> findByIdAndMemberIdAndStatus(Long answerId, Long memberId, Status status);
	Optional<Answer> findByIdAndQuestionIdAndMemberIdAndStatus(Long answerId, Long questionId, Long memberId, Status status);
	Optional<Answer> findByIdAndQuestionIdAndStatus(Long answerId, Long questionId, Status status);
	List<Answer> findTop20ByMemberIdAndStatusOrderByCreatedAtDesc(Long memberId, Status status);
	List<Answer> findByQuestionInAndSelectedAtIsNotNull(List<Question> content);
	Integer countAllByMemberIdAndSelectedAtIsNotNullAndStatus(Long memberId, Status status);
	long countByMemberIdAndStatus(Long memberId, Status status);
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = """
		UPDATE answer a
		SET a.status = 'INACTIVE'
		WHERE a.question_id = :questionId
	""", nativeQuery = true)
	void softDeleteAnswersByQuestionId(@Param(value = "questionId") Long questionId);
	Slice<Answer> findSliceByQuestionIdAndStatusOrderByCreatedAtDesc(Long questionId, Status status, Pageable pageable);
	@Query(value = """
		SELECT * FROM answer
		WHERE question_id = :questionId AND status = 'ACTIVE'
			AND created_at < (SELECT created_at FROM answer WHERE id = :lastAnswerId)
		ORDER BY created_at DESC
	""", nativeQuery = true)
	Slice<Answer> findSliceByIdLessThanAndQuestionIdAndStatusOrderByCreatedAtDesc(@Param(value = "lastAnswerId") Long lastAnswerId,
																				  @Param(value = "questionId") Long questionId,
																				  Pageable pageable);
	@Query(value = """
		SELECT *
		FROM answer
		WHERE id IN (
			SELECT MIN(id)
			FROM answer
			WHERE created_at BETWEEN :startDate AND :endDate
				AND selected_at IS NULL
				AND status = 'ACTIVE'
			GROUP BY question_id
			)
	""", nativeQuery = true)
	Slice<Answer> findFirstAnswersOfNotSolvedQuestion(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	int countByQuestionIdAndStatus(Long questionId, Status status);
    
	Optional<Answer> findTopByQuestionIdAndSelectedAtIsNullAndStatusOrderByLikeCountDescDislikeCountAscCreatedAtAsc(Long questionId, Status status);
}
