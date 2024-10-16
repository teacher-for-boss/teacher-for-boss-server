package kr.co.teacherforboss.repository;

import java.time.LocalDateTime;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.enums.Status;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
        WHERE CONCAT(LPAD(like_count, 10, '0'), LPAD(id, 10, '0')) <
            (SELECT CONCAT(LPAD(p.like_count, 10, '0'), LPAD(p.id, 10, '0'))
                FROM post p
                WHERE p.id = :postId)
            AND status = 'ACTIVE'
        ORDER BY like_count DESC, id DESC;
    """, nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByLikeCountDesc(@Param(value = "postId") Long postId, PageRequest pageRequest);
    @Query(value = """
        SELECT * FROM post
        WHERE CONCAT(LPAD(view_count, 10, '0'), LPAD(id, 10, '0')) <
            (SELECT CONCAT(LPAD(p.view_count, 10, '0'), LPAD(p.id, 10, '0'))
                FROM post p
                WHERE p.id = :postId)
            AND status = 'ACTIVE'
        ORDER BY view_count DESC, id DESC;
    """, nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByViewCountDesc(@Param(value = "postId") Long postId, PageRequest pageRequest);
    @Query(value = """
        SELECT * FROM post
        WHERE id <
            (SELECT id
                FROM post p
                WHERE p.id = :postId)
            AND status = 'ACTIVE'
        ORDER BY id DESC;
    """, nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByCreatedAtDesc(Long postId, PageRequest pageRequest);
    Slice<Post> findSliceByTitleContainingOrContentContainingAndStatusOrderByCreatedAtDesc(String titleKeyword, String contentKeyword, Status status, PageRequest pageRequest);
    @Query(value = """
        SELECT * FROM post
        WHERE (title LIKE CONCAT('%', :keyword, '%') OR content LIKE CONCAT('%', :keyword, '%'))
            AND created_at < (SELECT created_at FROM post WHERE id = :postId)
            AND status = 'ACTIVE'
        ORDER BY created_at DESC
    """, nativeQuery = true)
    Slice<Post> findSliceByIdLessThanAndKeywordOrderByCreatedAtDesc(String keyword, Long postId, PageRequest pageRequest);

    @Query(value = """
        SELECT p.* FROM post p
        WHERE p.id IN (
            SELECT c.post_id FROM comment c
            WHERE c.member_id = :memberId
        ) AND p.status = 'ACTIVE'
        AND (
            SELECT MAX(c.created_at) FROM comment c
            WHERE c.post_id = p.id
            AND c.member_id = :memberId
        ) < (
            SELECT MAX(c.created_at) FROM comment c
            WHERE c.post_id = :lastPostId
            AND c.member_id = :memberId
        )
        ORDER BY ( SELECT MAX(c.created_at) FROM comment c
        WHERE c.post_id = p.id AND c.member_id = :memberId
        ) DESC
    """, nativeQuery = true)
    Slice<Post> findCommentedPostsSliceByIdLessThanAndMemberIdOrderByCreatedAtDesc(Long memberId, Long lastPostId, PageRequest pageRequest);

    @Query(value = """
        SELECT * FROM post p
        WHERE p.id IN (
            SELECT comment.post_id FROM comment WHERE member_id = :memberId
            ) AND status = 'ACTIVE'
        ORDER BY (SELECT MAX(c.created_at) FROM comment c WHERE c.post_id = p.id AND c.member_id = :memberId) DESC
    """, nativeQuery = true)
    Slice<Post> findCommentedPostsSliceByMemberIdOrderByCreatedAtDesc(Long memberId, PageRequest pageRequest);

    //TODO: (다음 릴리즈) 게시글 작성일자 상관없이 최근 일주일 조회수 높은 순
    @Query(value = """
        SELECT * FROM post
        WHERE like_count >= 5
            AND created_at BETWEEN :startReqDate AND :endReqDate
            AND status = 'ACTIVE'
        ORDER BY view_count DESC, created_at DESC
        LIMIT 5
    """, nativeQuery = true)
    List<Post> findHotPosts(LocalDateTime startReqDate, LocalDateTime endReqDate);
    Slice<Post> findSliceByMemberIdAndStatusOrderByCreatedAtDesc(Long memberId, Status status, PageRequest pageRequest);
    @Query(value = """
        SELECT * FROM post
        WHERE member_id = :memberId AND status = 'ACTIVE'
            AND id <
            (SELECT id
                FROM post p
                WHERE p.id = :postId)
        ORDER BY id DESC
    """, nativeQuery = true)
    Slice<Post> findSliceByIdLessThanAndMemberIdOrderByCreatedAtDesc(Long postId, Long memberId, PageRequest pageRequest);
    @Query(value = """
        SELECT * FROM post
        WHERE id IN (SELECT pb.post_id FROM post_bookmark pb WHERE pb.member_id = :memberId AND pb.bookmarked = 'T')
            AND status = 'ACTIVE'
        ORDER BY created_at DESC
    """, nativeQuery = true)
    Slice<Post> findBookmarkedPostsSliceByMemberIdOrderByCreatedAtDesc(Long memberId, PageRequest pageRequest);
    @Query(value = """
        SELECT * FROM post
        WHERE id IN (SELECT pb.post_id FROM post_bookmark pb WHERE pb.member_id = :memberId AND pb.bookmarked = 'T')
        AND status = 'ACTIVE'
        AND id <
            (SELECT id
                FROM post p
                WHERE p.id = :postId)
        ORDER BY id DESC
    """, nativeQuery = true)
    Slice<Post> findBookmarkedPostsSliceByIdLessThanAndMemberIdOrderByCreatedAtDesc(Long postId, Long memberId, PageRequest pageRequest);
}
