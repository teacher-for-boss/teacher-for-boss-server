package kr.co.teacherforboss.repository;

import java.util.List;
import java.util.Optional;
import kr.co.teacherforboss.domain.QuestionChoice;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionChoiceRepository extends JpaRepository<QuestionChoice, Long> {
    Optional<QuestionChoice> findByIdAndStatus(Long questionChoiceId, Status status);
    List<QuestionChoice> findByIdInAndStatus(List<Long> idCollect, Status status);
}
