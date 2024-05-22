package kr.co.teacherforboss.repository;

import java.util.List;
import java.util.Optional;

import kr.co.teacherforboss.domain.ProblemChoice;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemChoiceRepository extends JpaRepository<ProblemChoice, Long> {
    Optional<ProblemChoice> findByIdAndStatus(Long problemChoiceId, Status status);
    List<ProblemChoice> findByIdInAndStatus(List<Long> idCollect, Status status);
}
