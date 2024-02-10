package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.QuestionChoice;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionChoiceRepository extends JpaRepository<QuestionChoice, Long> {
    Optional<QuestionChoice> findByIdAndStatus(Long questionChoiceId, Status status);
}
