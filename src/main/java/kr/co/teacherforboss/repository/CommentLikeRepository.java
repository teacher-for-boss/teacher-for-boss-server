package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        UPDATE comment_like
        SET status = 'INACTIVE'
        WHERE comment_id IN (:commentIds);
    """, nativeQuery = true)
    void softDeleteCommentLikeByComments(@Param(value = "commentIds") List<Long> commentIds);
}
