package kr.co.teacherforboss.repository;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.Status;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByIdAndStatus(Long questionId, Status status);
    Optional<Question> findByIdAndMemberIdAndStatus(Long questionId, Long memberId, Status status);
    boolean existsByIdAndStatus(Long questionId, Status status);
	Integer countAllByCategoryIdAndStatus(Long categoryId, Status status);
	@Query(value = """
			SELECT * FROM question 
			WHERE category_id = :categoryId and status = 'ACTIVE'
			ORDER BY like_count DESC, created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByLikeCountDesc(Long categoryId, PageRequest pageRequest);
	@Query(value = """
			SELECT * FROM question 
			WHERE category_id = :categoryId and status = 'ACTIVE'
			ORDER BY view_count DESC, created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByViewCountDesc(Long categoryId, PageRequest pageRequest);
	@Query(value = """
			SELECT * FROM question 
			WHERE category_id = :categoryId and status = 'ACTIVE'
			ORDER BY created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByCreatedAtDesc(Long categoryId, PageRequest pageRequest);
	@Query(value = """
            SELECT * FROM question
            WHERE category_id = :categoryId AND status = 'ACTIVE' 
            	AND (like_count <= (SELECT like_count FROM question WHERE id = :questionId) AND id != :questionId)
            ORDER BY like_count DESC, created_at DESC
    """, nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByLikeCountDescWithLastQuestionId(Long categoryId, Long questionId, PageRequest pageRequest);
	@Query(value = """
            SELECT * FROM question
            WHERE category_id = :categoryId AND status = 'ACTIVE' 
            	AND (view_count <= (SELECT view_count FROM question WHERE id = :questionId) AND id != :questionId)
            ORDER BY view_count DESC, created_at DESC
    """, nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByViewCountDescWithLastQuestionId(Long categoryId, Long questionId, PageRequest pageRequest);
	@Query(value = """
			SELECT * FROM question
			WHERE category_id = :categoryId AND status = 'ACTIVE' 
				AND created_at < (SELECT created_at FROM question WHERE id = :questionId)
			ORDER BY created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByCreatedAtDescWithLastQuestionId(Long categoryId, Long questionId, PageRequest pageRequest);
	Integer countAllByTitleContainingAndStatus(String keyword, Status status);
	@Query(value = """
			SELECT * FROM question
			WHERE title LIKE CONCAT('%', :keyword, '%') AND status = 'ACTIVE' 
			ORDER BY created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByTitleContainingOrderByCreatedAtDesc(String keyword, PageRequest pageRequest);
	@Query(value = """
			SELECT * FROM question
			WHERE title LIKE CONCAT('%', :keyword, '%') AND status = 'ACTIVE' 
				AND created_at < (SELECT created_at FROM question WHERE id = :questionId)
			ORDER BY created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByTitleContainingOrderByCreatedAtDescWithLastQuestionId(String keyword, Long questionId, PageRequest pageRequest);
}
