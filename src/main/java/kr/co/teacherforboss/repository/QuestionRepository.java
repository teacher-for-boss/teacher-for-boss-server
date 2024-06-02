package kr.co.teacherforboss.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.Status;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
	Optional<Question> findByIdAndMember(Long questionId, Member member);
	Optional<Question> findByIdAndStatus(Long questionId, Status status);
}
