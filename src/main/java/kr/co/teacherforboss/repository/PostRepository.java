package kr.co.teacherforboss.repository;

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
            WHERE ((like_count < (SELECT like_count FROM post WHERE id = :postId)
                OR (like_count = (SELECT like_count FROM post WHERE id = :postId) AND id != :postId)))
                AND status = 'ACTIVE'
            ORDER BY like_count DESC, created_at DESC;
    """, nativeQuery = true)
    Slice<Post> findSliceByIdLessThanOrderByLikeCountDesc(@Param(value = "postId") Long postId, PageRequest pageRequest);

    @Query(value = """
            SELECT * FROM post
            WHERE ((view_count < (SELECT view_count FROM post WHERE id = :postId)
                OR (view_count = (SELECT view_count FROM post WHERE id = :postId) AND id != :postId)))
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

    @Query(value = """
            SELECT * FROM post
            WHERE like_count >= 5 AND status = 'ACTIVE'
            ORDER BY view_count DESC, created_at DESC
            LIMIT 5
    """, nativeQuery = true)
    List<Post> findHotPosts(); //TODO: 최근 일주일
    @Query(value = """
        SELECT * FROM post
        WHERE member_id = :memberId AND status = 'ACTIVE'
        ORDER BY created_at DESC
    """, nativeQuery = true)
    Slice<Post> findMyPostsSliceByMemberIdOrderByCreatedAtDesc(Long memberId, PageRequest pageRequest);
    @Query(value = """
        SELECT * FROM post
        WHERE member_id = :memberId AND status = 'ACTIVE'
            AND created_at < (SELECT created_at FROM post WHERE id = :postId)
        ORDER BY created_at DESC
    """, nativeQuery = true)
    Slice<Post> findMyPostsSliceByMemberIdAndIdLessThanOrderByCreatedAtDesc(Long memberId, Long postId, PageRequest pageRequest);
}
