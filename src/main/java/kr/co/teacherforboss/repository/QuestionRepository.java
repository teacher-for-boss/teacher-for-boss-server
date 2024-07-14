package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByIdAndStatus(Long questionId, Status status);
    Optional<Question> findByIdAndMemberIdAndStatus(Long questionId, Long memberId, Status status);
    boolean existsByIdAndStatus(Long questionId, Status status);
	Slice<Question> findSliceByStatusOrderByLikeCountDescCreatedAtDesc(Status status, PageRequest pageRequest);
	Slice<Question> findSliceByStatusOrderByViewCountDescCreatedAtDesc(Status status, PageRequest pageRequest);
	Slice<Question> findSliceByStatusOrderByCreatedAtDesc(Status status, PageRequest pageRequest);
	Slice<Question> findSliceByCategoryIdAndStatusOrderByLikeCountDescCreatedAtDesc(Long categoryId, Status status, PageRequest pageRequest);
	Slice<Question> findSliceByCategoryIdAndStatusOrderByViewCountDescCreatedAtDesc(Long categoryId, Status status, PageRequest pageRequest);
	Slice<Question> findSliceByCategoryIdAndStatusOrderByCreatedAtDesc(Long categoryId, Status status, PageRequest pageRequest);
	@Query(value = """
		SELECT * FROM question
		WHERE category_id = :categoryId AND status = 'ACTIVE'
			AND (like_count <= (SELECT like_count FROM question WHERE id = :questionId) AND id != :questionId)
		ORDER BY like_count DESC, created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByLikeCountDesc(@Param(value = "categoryId") Long categoryId, @Param(value = "questionId") Long questionId, PageRequest pageRequest);
	@Query(value = """
		SELECT * FROM question
		WHERE category_id = :categoryId AND status = 'ACTIVE'
			AND (view_count <= (SELECT view_count FROM question WHERE id = :questionId) AND id != :questionId)
		ORDER BY view_count DESC, created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByViewCountDesc(@Param(value = "categoryId") Long categoryId, @Param(value = "questionId") Long questionId, PageRequest pageRequest);
	@Query(value = """
		SELECT * FROM question
		WHERE status = 'ACTIVE'
			AND created_at < (SELECT created_at FROM question WHERE id = :questionId)
		ORDER BY created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByCreatedAtDesc(@Param(value = "questionId") Long questionId, PageRequest pageRequest);
	@Query(value = """
		SELECT * FROM question
		WHERE status = 'ACTIVE'
			AND (like_count <= (SELECT like_count FROM question WHERE id = :questionId) AND id != :questionId)
		ORDER BY like_count DESC, created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByLikeCountDesc(@Param(value = "questionId") Long questionId, PageRequest pageRequest);
	@Query(value = """
		SELECT * FROM question
		WHERE status = 'ACTIVE'
			AND (view_count <= (SELECT view_count FROM question WHERE id = :questionId) AND id != :questionId)
		ORDER BY view_count DESC, created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByViewCountDesc(@Param(value = "questionId") Long questionId, PageRequest pageRequest);
	@Query(value = """
		SELECT * FROM question
		WHERE category_id = :categoryId AND status = 'ACTIVE'
			AND created_at < (SELECT created_at FROM question WHERE id = :questionId)
		ORDER BY created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByCreatedAtDesc(@Param(value = "categoryId") Long categoryId, @Param(value = "questionId") Long questionId, PageRequest pageRequest);
	Slice<Question> findSliceByTitleContainingOrContentContainingAndStatusOrderByCreatedAtDesc(String titleKeyword, String contentKeyword, Status status, PageRequest pageRequest);
	@Query(value = """
			SELECT * FROM question
			WHERE (title LIKE CONCAT('%', :keyword, '%') OR content LIKE CONCAT('%', :keyword, '%')) AND status = 'ACTIVE'
				AND created_at < (SELECT created_at FROM question WHERE id = :questionId)
			ORDER BY created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByIdLessThanTitleContainingOrderByCreatedAtDesc(String keyword, Long questionId, PageRequest pageRequest);
    Slice<Question> findSliceByStatusAndMemberIdOrderByCreatedAtDesc(Status status, Long memberId, PageRequest pageRequest);
	@Query(value = """
			SELECT * FROM question
			WHERE member_id = :memberId AND status = 'ACTIVE'
				AND created_at < (SELECT created_at FROM question WHERE id = :questionId)
			ORDER BY created_at DESC
	""", nativeQuery = true)
	Slice<Question> findSliceByMemberIdAndIdLessThanOrderByCreatedAtDesc(@Param(value = "questionId") Long questionId, Long memberId, PageRequest pageRequest);
}
