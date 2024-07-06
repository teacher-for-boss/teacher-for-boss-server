package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Comment;
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

    Slice<Comment> findSliceByPostIdAndParentIdIsNullAndStatusOrderByCreatedAtDesc(Long postId, Pageable pageable, Status status);

    @Query(value = """
        SELECT * FROM comment
        WHERE post_id = :postId
          AND parent_id IS NULL
          AND created_at < (SELECT created_at FROM comment WHERE id = :lastCommentId)
          AND status = 'ACTIVE'
        ORDER BY created_at DESC
    """, nativeQuery = true)
    Slice<Comment> findSliceByIdLessThanAndPostIdAndParentIdIsNullOrderByCreatedAtDesc(
            @Param("postId") Long postId,
            @Param("lastCommentId") Long lastCommentId,
            Pageable pageable);

    @Query(value = """
        SELECT * FROM comment
        WHERE post_id = :postId
            AND parent_id IN :parentIds
            AND status = 'ACTIVE'
        ORDER BY created_at DESC
    """, nativeQuery = true)
    List<Comment> findAllByPostIdAndParentIdInAndStatus(@Param("postId") Long postId,
                                                        @Param("parentIds") List<Long> parentIds);
}
