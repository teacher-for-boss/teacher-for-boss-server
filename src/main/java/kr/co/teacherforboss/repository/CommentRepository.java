package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findByIdAndStatus(Long id, Status status);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE comment
        SET status = 'INACTIVE'
        WHERE post_id = :postId
    """, nativeQuery = true)
    void softDeleteCommentsByPostId(@Param(value = "postId") Long postId);
    Optional<Comment> findByIdAndPostAndStatus(Long id, Post post, Status status);
}
