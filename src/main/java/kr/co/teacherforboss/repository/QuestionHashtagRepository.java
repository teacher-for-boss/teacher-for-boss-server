package kr.co.teacherforboss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.QuestionHashtag;

@Repository
public interface QuestionHashtagRepository extends JpaRepository<QuestionHashtag, Long> {
	@Modifying(clearAutomatically = true)
	@Query(value = """
			UPDATE question_hashtag qh 
			SET qh.status = 'INACTIVE'
			WHERE qh.question_id = :questionId
			""", nativeQuery = true)
	void softDeleteAllByQuestionId(@Param("questionId") Long questionId);
}
