package kr.co.teacherforboss.repository;

import java.util.Optional;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long> {
    Optional<PostBookmark> findByPostIdAndMemberIdAndStatus(Long postId, Long memberId, Status status);
    List<PostBookmark> findByPostInAndStatus(List<Post> posts, Status status);
}
