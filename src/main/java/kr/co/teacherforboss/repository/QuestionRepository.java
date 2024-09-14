package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByIdAndStatus(Long questionId, Status status);
    Optional<Question> findByIdAndMemberIdAndStatus(Long questionId, Long memberId, Status status);
    boolean existsByIdAndStatus(Long questionId, Status status);
	long countByMemberIdAndStatus(Long memberId, Status status);
	Slice<Question> findSliceByStatusOrderByLikeCountDescCreatedAtDesc(Status status, PageRequest pageRequest);
	Slice<Question> findSliceByStatusOrderByViewCountDescCreatedAtDesc(Status status, PageRequest pageRequest);
	Slice<Question> findSliceByStatusOrderByCreatedAtDesc(Status status, PageRequest pageRequest);
	Slice<Question> findSliceByCategoryIdAndStatusOrderByLikeCountDescCreatedAtDesc(Long categoryId, Status status, PageRequest pageRequest);
	Slice<Question> findSliceByCategoryIdAndStatusOrderByViewCountDescCreatedAtDesc(Long categoryId, Status status, PageRequest pageRequest);
	Slice<Question> findSliceByCategoryIdAndStatusOrderByCreatedAtDesc(Long categoryId, Status status, PageRequest pageRequest);
	@Query(value = """
    	SELECT * FROM question
    	WHERE category_id = :categoryId AND
    		CONCAT(LPAD(like_count, 10, '0'), LPAD(id, 10, '0')) <
    		(SELECT CONCAT(LPAD(q.like_count, 10, '0'), LPAD(q.id, 10, '0'))
    			FROM question q
    			WHERE q.id = :questionId)
    		AND status = 'ACTIVE'
    	ORDER BY like_count DESC, id DESC;
    """, nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByLikeCountDesc(@Param(value = "categoryId") Long categoryId, @Param(value = "questionId") Long questionId, PageRequest pageRequest);
	@Query(value = """
        SELECT * FROM question
        WHERE category_id = :categoryId AND 
        	CONCAT(LPAD(view_count, 10, '0'), LPAD(id, 10, '0')) <
        	(SELECT CONCAT(LPAD(q.view_count, 10, '0'), LPAD(q.id, 10, '0'))
        		FROM question q
        		WHERE q.id = :questionId)
        	AND status = 'ACTIVE'
        ORDER BY view_count DESC, id DESC;
    """, nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByViewCountDesc(@Param(value = "categoryId") Long categoryId, @Param(value = "questionId") Long questionId, PageRequest pageRequest);
	@Query(value = """
        SELECT * FROM question
        WHERE id <
        	(SELECT q.id
        		FROM question q
        		WHERE q.id = :questionId)
        	AND status = 'ACTIVE'
        ORDER BY id DESC;
    """, nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByCreatedAtDesc(@Param(value = "questionId") Long questionId, PageRequest pageRequest);
	@Query(value = """
    	SELECT * FROM question
    	WHERE CONCAT(LPAD(like_count, 10, '0'), LPAD(id, 10, '0')) <
    		(SELECT CONCAT(LPAD(q.like_count, 10, '0'), LPAD(q.id, 10, '0'))
    			FROM question q
    			WHERE q.id = :questionId)
    		AND status = 'ACTIVE'
    	ORDER BY like_count DESC, id DESC;
    """, nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByLikeCountDesc(@Param(value = "questionId") Long questionId, PageRequest pageRequest);
	@Query(value = """
    	SELECT * FROM question
    	WHERE CONCAT(LPAD(view_count, 10, '0'), LPAD(id, 10, '0')) <
    		(SELECT CONCAT(LPAD(q.view_count, 10, '0'), LPAD(q.id, 10, '0'))
    			FROM question q
    			WHERE q.id = :questionId)
    		AND status = 'ACTIVE'
    	ORDER BY view_count DESC, id DESC;
    """, nativeQuery = true)
	Slice<Question> findSliceByIdLessThanOrderByViewCountDesc(@Param(value = "questionId") Long questionId, PageRequest pageRequest);
	@Query(value = """
        SELECT * FROM question
        WHERE category_id = :categoryId AND 
        	id <
        	(SELECT id
        		FROM question q
        		WHERE q.id = :questionId)
        	AND status = 'ACTIVE'
        ORDER BY id DESC;
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
	Slice<Question> findSliceByMemberIdAndStatusOrderByCreatedAtDesc(Long memberId, Status status, PageRequest pageRequest);
	@Query(value = """
    	SELECT * FROM question
    	WHERE LPAD(id, 10, '0') <
    		(SELECT LPAD(q.id, 10, '0')
    			FROM question q
    			WHERE q.id = :questionId)
    		AND member_id = :memberId AND status = 'ACTIVE'
    	ORDER BY id DESC;
    """, nativeQuery = true)
	Slice<Question> findSliceByIdLessThanAndMemberIdOrderByCreatedAtDesc(Long questionId, Long memberId, PageRequest pageRequest);

	@Query(value = """
		SELECT q.* FROM question q
		WHERE q.id IN (
			SELECT a.question_id FROM answer a
			WHERE a.member_id = :memberId
		) AND q.status = 'ACTIVE'
		AND (
			SELECT MAX(a.created_at) FROM answer a
			WHERE a.question_id = q.id
			AND a.member_id = :memberId
		) < (
			SELECT MAX(a.created_at) FROM answer a
			WHERE a.question_id = :lastQuestionId
			AND a.member_id = :memberId
		)
		ORDER BY ( SELECT MAX(a.created_at) FROM answer a
			WHERE a.question_id = q.id AND a.member_id = :memberId
		) DESC
	""", nativeQuery = true)
	Slice<Question> findAnsweredQuestionsSliceByIdLessThanAndMemberIdOrderByCreatedAtDesc(Long memberId, Long lastQuestionId, PageRequest pageRequest);

	@Query(value = """
    	SELECT * FROM question q
    	WHERE q.id IN (
    		SELECT answer.question_id FROM answer WHERE member_id = :memberId
    		) AND status = 'ACTIVE'
    	ORDER BY (SELECT MAX(a.created_at) FROM answer a WHERE a.question_id = q.id AND a.member_id = :memberId) DESC
    """, nativeQuery = true)
	Slice<Question> findAnsweredQuestionsSliceByMemberIdOrderByCreatedAtDesc(Long memberId, PageRequest pageRequest);

	@Query(value = """
  		SELECT * FROM question
  		WHERE status = 'ACTIVE'
  		ORDER BY view_count DESC, created_at DESC LIMIT 5
  	""", nativeQuery = true)
	List<Question> findHotQuestions(); // TODO: 최근 일주일
	@Query(value = """
  		SELECT * FROM question
  		WHERE id IN (SELECT qb.question_id FROM question_bookmark qb WHERE qb.member_id = :memberId AND qb.bookmarked = 'T')
  			AND status = 'ACTIVE'
  		ORDER BY created_at DESC
  	""", nativeQuery = true)
	Slice<Question> findBookmarkedQuestionsSliceByMemberIdOrderByCreatedAtDesc(Long memberId, PageRequest pageRequest);
	@Query(value = """
  		SELECT * FROM question
  		WHERE id IN (SELECT qb.question_id FROM question_bookmark qb WHERE qb.member_id = :memberId AND qb.bookmarked = 'T')
  			AND status = 'ACTIVE'
  			AND id <
  			(SELECT id
  				FROM question q
  				WHERE q.id = :questionId)
  		ORDER BY id DESC
  	""", nativeQuery = true)
	Slice<Question> findBookmarkedQuestionsSliceByIdLessThanAndMemberIdOrderByCreatedAtDesc(Long questionId, Long memberId, PageRequest pageRequest);
}
