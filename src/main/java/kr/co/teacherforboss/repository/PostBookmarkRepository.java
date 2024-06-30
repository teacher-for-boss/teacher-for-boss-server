package kr.co.teacherforboss.repository;

import java.util.Optional;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long> {
    Optional<PostBookmark> findByPostIdAndMemberIdAndStatus(Long postId, Long memberId, Status status);
    List<PostBookmark> findByPostInAndMemberIdAndStatus(List<Post> posts, Long memberId, Status status);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
		UPDATE post_bookmark
        SET status = 'INACTIVE'
        WHERE post_id = :postId
    """, nativeQuery = true)
    void softDeletePostBookmarksByPostId(@Param(value = "postId") Long postId);
}
