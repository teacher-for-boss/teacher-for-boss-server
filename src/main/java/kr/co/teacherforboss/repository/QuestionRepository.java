package kr.co.teacherforboss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
	Optional<Question> findByIdAndMember(Long questionId, Member member);
}
