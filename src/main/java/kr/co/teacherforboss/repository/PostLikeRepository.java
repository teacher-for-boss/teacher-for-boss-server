package kr.co.teacherforboss.repository;

import java.util.Optional;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostIdAndMemberIdAndStatus(Long postId, Long memberId, Status status);
    List<PostLike> findByPostInAndStatus(List<Post> postCollect, Status status);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
		update post_like
        set status = 'INACTIVE'
        where comment_id in (select comment_id from comment where post_id = :postId);
	""", nativeQuery = true)
    void softDeletePostLikeByPostId(@Param(value = "postId") Long postId);
}
