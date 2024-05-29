package kr.co.teacherforboss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.QuestionHashtag;

@Repository
public interface QuestionHashtagRepository extends JpaRepository<QuestionHashtag, Long> {
	@Modifying
	@Query(value = "delete from question_hashtag qh "
			+ "where qh.question_id = :questionId", nativeQuery = true)
	void deleteAllByQuestionId(@Param("questionId") Long questionId);
}
