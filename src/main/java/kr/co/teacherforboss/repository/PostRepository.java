package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndStatus(Long postId, Status status);
    Optional<Post> findByIdAndMemberIdAndStatus(Long postId, Long memberId, Status status);
    Integer countAllByStatus(Status status);
    @Query(value = "select * from post order by like_count desc, created_at asc", nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByLikeCountDesc(PageRequest pageRequest);
    @Query(value = "select * from post order by view_count desc, created_at asc", nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByViewCountDesc(PageRequest pageRequest);
    @Query(value = "select * from post order by created_at desc, created_at asc", nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByCreatedAtDesc(PageRequest pageRequest);
    @Query(value = """
            SELECT * FROM post
            WHERE (like_count < (SELECT like_count FROM post WHERE id = :postId)
                OR (like_count = (SELECT like_count FROM post WHERE id = :postId) AND id > :postId))
            ORDER BY like_count DESC, created_at asc;
    """, nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByLikeCountDescWithLastPostId(Long postId, PageRequest pageRequest);
    @Query(value = """
            SELECT * FROM post
            WHERE (view_count < (SELECT view_count FROM post WHERE id = :postId)
                OR (view_count = (SELECT view_count FROM post WHERE id = :postId) AND id > :postId))
            ORDER BY view_count DESC, created_at asc;
    """, nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByViewCountDescWithLastPostId(Long postId, PageRequest pageRequest);
    @Query(value = "select * from post where created_at < (select created_at from post where id = :postId) order by created_at desc", nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByCreatedAtDescWithLastPostId(Long postId, PageRequest pageRequest);
}
