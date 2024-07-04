package kr.co.teacherforboss.repository;

import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsByIdAndStatus(Long questionId, Status status);
    Optional<Post> findByIdAndStatus(Long postId, Status status);
    Optional<Post> findByIdAndMemberIdAndStatus(Long postId, Long memberId, Status status);
    Slice<Post> findSliceByStatusOrderByLikeCountDesc(Status status, PageRequest pageRequest);
    Slice<Post> findSliceByStatusOrderByViewCountDesc(Status status, PageRequest pageRequest);
    Slice<Post> findSliceByStatusOrderByCreatedAtDesc(Status status, PageRequest pageRequest);
    @Query(value = """
            SELECT * FROM post
            WHERE ((like_count < (SELECT like_count FROM post WHERE id = :postId)
                OR (like_count = (SELECT like_count FROM post WHERE id = :postId) AND id > :postId)))
                AND status = 'ACTIVE'
            ORDER BY like_count DESC, created_at DESC;
    """, nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByLikeCountDesc(@Param(value = "postId") Long postId, PageRequest pageRequest);

    @Query(value = """
            SELECT * FROM post
            WHERE ((view_count < (SELECT view_count FROM post WHERE id = :postId)
                OR (view_count = (SELECT view_count FROM post WHERE id = :postId) AND id > :postId)))
                AND status = 'ACTIVE'
            ORDER BY view_count DESC, created_at DESC;
    """, nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByViewCountDesc(@Param(value = "postId") Long postId, PageRequest pageRequest);

    @Query(value = """
            SELECT * FROM post 
            WHERE created_at < (SELECT created_at FROM post WHERE id = :postId) 
                AND status = 'ACTIVE'
            ORDER BY created_at DESC
    """, nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByCreatedAtDesc(@Param(value = "postId") Long postId, PageRequest pageRequest);
}
