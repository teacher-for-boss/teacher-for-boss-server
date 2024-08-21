package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionBookmark;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionBookmarkRepository extends JpaRepository<QuestionBookmark, Long> {
	Optional<QuestionBookmark> findByQuestionIdAndMemberIdAndStatus(Long questionId, Long memberId, Status status);
	List<QuestionBookmark> findByQuestionInAndMemberIdAndStatus(List<Question> questions, Long memberId, Status status);
    long countByMemberIdAndBookmarkedAndStatus(Long memberId, BooleanType bookmarked, Status status);
}
