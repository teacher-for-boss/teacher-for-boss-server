package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Integer countAllByPostAndStatus(Post post, Status status);
    Comment findByIdAndStatus(Long id, Status status);
    boolean existsByIdAndStatus(Long id, Status status);
}
