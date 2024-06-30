package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
		UPDATE post_hashtag p
		SET p.status = 'INACTIVE'
		WHERE p.post_id = :postId
	""", nativeQuery = true)
    void softDeletePostHashtagByPostId(@Param(value = "postId") Long postId);
}
