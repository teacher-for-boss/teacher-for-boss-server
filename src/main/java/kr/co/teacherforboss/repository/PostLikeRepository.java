package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    PostLike findByPostAndMemberAndStatus(Post post, Member member, Status status);
    List<PostLike> findByPostInAndStatus(List<Post> postCollect, Status status);
}
