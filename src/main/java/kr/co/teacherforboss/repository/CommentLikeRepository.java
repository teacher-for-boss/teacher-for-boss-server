package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.CommentLike;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE comment_like
        SET status = 'INACTIVE'
        WHERE comment_id IN (:commentIds);
    """, nativeQuery = true)
    void softDeleteCommentLikeByComments(@Param(value = "commentIds") List<Long> commentIds);
    Optional<CommentLike> findByCommentIdAndMemberIdAndStatus(Long commentId, Long memberId, Status status);
}
