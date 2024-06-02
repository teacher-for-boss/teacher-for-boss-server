package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Integer countAllByPostAndStatus(Post post, Status status);
    List<Comment> findAllByPostAndStatus(Post post, Status status);
}
