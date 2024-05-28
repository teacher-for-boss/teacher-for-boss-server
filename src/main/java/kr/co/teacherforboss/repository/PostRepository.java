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
    Integer countAllByStatus(Status status);
    @Query(value = "select * from (select * from post order by like_count desc) p", nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByLikeCountDesc(Long postId, PageRequest pageRequest);
    @Query(value = "select * from (select * from post order by view_count desc) p", nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByViewCountDesc(Long postId, PageRequest pageRequest);
    @Query(value = "select * from (select * from post order by created_at desc) p", nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByCreatedAtDesc(Long postId, PageRequest pageRequest);
}
