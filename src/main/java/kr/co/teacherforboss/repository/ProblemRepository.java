package kr.co.teacherforboss.repository;

import java.util.List;
import java.util.Optional;

import kr.co.teacherforboss.domain.Problem;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Integer countByExamIdAndStatus(Long examId, Status status);
    Optional<Problem> findByIdAndStatus(Long problemId, Status status);
    List<Problem> findAllByExamIdAndStatus(Long examId, Status status, Sort sort);
    List<Problem> findByIdInAndStatus(List<Long> idCollect, Status status);
}
