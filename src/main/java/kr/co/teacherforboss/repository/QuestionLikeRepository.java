package kr.co.teacherforboss.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionLike;
import kr.co.teacherforboss.domain.enums.Status;

@Repository
public interface QuestionLikeRepository extends JpaRepository<QuestionLike, Long> {
	Optional<QuestionLike> findByQuestionIdAndMemberIdAndStatus(Long questionId, Long memberId, Status status);

	List<QuestionLike> findByQuestionInAndStatus(List<Question> questions, Status status);
}
