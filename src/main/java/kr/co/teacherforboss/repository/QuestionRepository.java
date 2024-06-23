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
			WHERE category_id = :categoryId
			ORDER BY like_count DESC, created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByLikeCountDesc(Long categoryId, PageRequest pageRequest);
	@Query(value = """
			SELECT * FROM question 
			WHERE category_id = :categoryId 
			ORDER BY view_count DESC, created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByViewCountDesc(Long categoryId, PageRequest pageRequest);
	@Query(value = """
			SELECT * FROM question 
			WHERE category_id = :categoryId 
			ORDER BY created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByCreatedAtDesc(Long categoryId, PageRequest pageRequest);
	@Query(value = """
            SELECT * FROM question
            WHERE category_id = :categoryId AND (like_count <= (SELECT like_count FROM question WHERE id = :questionId) AND id != :questionId)
            ORDER BY like_count DESC, created_at DESC
    """, nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByLikeCountDescWithLastPostId(Long categoryId, Long questionId, PageRequest pageRequest);
	@Query(value = """
            SELECT * FROM question
            WHERE category_id = :categoryId AND (view_count <= (SELECT view_count FROM question WHERE id = :questionId) AND id != :questionId)
            ORDER BY view_count DESC, created_at DESC
    """, nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByViewCountDescWithLastPostId(Long categoryId, Long questionId, PageRequest pageRequest);
	@Query(value = """
			SELECT * FROM question
			WHERE category_id = :categoryId AND created_at < (SELECT created_at FROM question WHERE id = :questionId)
			ORDER BY created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByCreatedAtDescWithLastPostId(Long categoryId, Long questionId, PageRequest pageRequest);
}
