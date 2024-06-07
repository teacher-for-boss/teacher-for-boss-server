package kr.co.teacherforboss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.teacherforboss.domain.Category;
import kr.co.teacherforboss.domain.enums.Status;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	Category findAllByIdAndStatus(Long categoryId, Status status);
	Category findByIdAndStatus(Long categoryId, Status status);
}
