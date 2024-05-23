package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.ExamTag;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamTagRepository extends JpaRepository<ExamTag, Long> {
    boolean existsByIdAndExamCategoryIdAndStatus(Long id, Long examCategoryId, Status status);
    List<ExamTag> findExamTagsByExamCategoryIdAndStatus(Long categoryId, Status status);
}
