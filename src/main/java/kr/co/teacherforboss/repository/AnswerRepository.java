package kr.co.teacherforboss.repository;

import java.util.List;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.enums.Status;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
	List<Answer> findAllByQuestionIdAndStatus(Long questionId, Status status);
	Optional<Answer> findByIdAndMemberIdAndStatus(Long answerId, Long memberId, Status status);
	Optional<Answer> findByIdAndQuestionIdAndMemberIdAndStatus(Long answerId, Long questionId, Long memberId, Status status);
}
