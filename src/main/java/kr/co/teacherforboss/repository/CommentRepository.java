package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    Optional<Comment> findByIdAndPostIdAndStatus(Long commentId, Long postId, Status status);
    Integer countAllByPostAndStatus(Post post, Status status);
    List<Comment> findAllByPostIdAndStatus(Long postId, Status status);
    Optional<Comment> findByIdAndPostAndStatus(Long id, Post post, Status status);

    Slice<Comment> findSliceByParentIsNullAndStatusOrderByCreatedAtDesc(Status status, Pageable pageable);

    @Query(value = """
        SELECT * FROM comment
        WHERE id < :lastCommentId
          AND parent_id IS NULL
          AND status = :status
        ORDER BY created_at DESC
    """, nativeQuery = true)
    Slice<Comment> findSliceByIdLessThanAndParentIsNullAndStatusOrderByCreatedAtDesc(
            @Param("lastCommentId") Long lastCommentId,
            @Param("status") Status status,
            Pageable pageable);

    List<Comment> findAllByParentIdInAndStatus(List<Long> parentIds, Status status);
}
