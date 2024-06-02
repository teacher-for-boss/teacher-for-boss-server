package kr.co.teacherforboss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionBookmark;
import kr.co.teacherforboss.domain.enums.Status;

@Repository
public interface QuestionBookmarkRepository extends JpaRepository<QuestionBookmark, Long> {
	QuestionBookmark findByQuestionAndMemberAndStatus(Question questionToBookmark, Member member, Status status);
}
