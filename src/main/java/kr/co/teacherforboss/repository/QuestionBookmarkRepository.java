package kr.co.teacherforboss.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionBookmark;
import kr.co.teacherforboss.domain.enums.Status;

@Repository
public interface QuestionBookmarkRepository extends JpaRepository<QuestionBookmark, Long> {
	Optional<QuestionBookmark> findByQuestionIdAndMemberIdAndStatus(Long questionId, Long memberId, Status status);
	List<QuestionBookmark> findByQuestionInAndMemberIdAndStatus(List<Question> questions, Long memberId, Status status);
	List<QuestionBookmark> findByQuestionInAndStatus(List<Question> questions, Status status);
}
