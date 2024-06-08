package kr.co.teacherforboss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
