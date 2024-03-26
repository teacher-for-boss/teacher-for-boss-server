package kr.co.teacherforboss.repository;

import java.util.List;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamCategoryRepository extends JpaRepository<ExamCategory, Long> {

    boolean existsByIdAndStatus(Long id, Status status);
    List<ExamCategory> findExamCategoriesByStatus(Status status);
}
