package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Tag;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByIdAndStatus(Long id, Status status);
    List<Tag> findTagsByExamCategoryIdAndStatus(Long categoryId, Status status);
}
