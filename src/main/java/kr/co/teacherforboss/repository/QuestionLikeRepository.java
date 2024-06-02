package kr.co.teacherforboss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionLike;
import kr.co.teacherforboss.domain.enums.Status;

@Repository
public interface QuestionLikeRepository extends JpaRepository<QuestionLike, Long> {
	QuestionLike findByQuestionAndMemberAndStatus(Question questionToLike, Member member, Status status);
}
