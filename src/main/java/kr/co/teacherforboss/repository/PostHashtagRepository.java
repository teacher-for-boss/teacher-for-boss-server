package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostHashtag;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
    List<PostHashtag> findAllByPostAndStatus(Post post, Status status);
}
