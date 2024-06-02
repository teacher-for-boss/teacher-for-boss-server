package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.CommentLike;
import kr.co.teacherforboss.domain.Hashtag;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostHashtag;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.CommentLikeRepository;
import kr.co.teacherforboss.repository.CommentRepository;
import kr.co.teacherforboss.repository.HashtagRepository;
import kr.co.teacherforboss.repository.PostBookmarkRepository;
import kr.co.teacherforboss.repository.PostHashtagRepository;
import kr.co.teacherforboss.repository.PostLikeRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BoardCommandServiceImpl implements BoardCommandService {
    private final AuthCommandService authCommandService;
    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    @Transactional
    public Post savePost(BoardRequestDTO.SavePostDTO request) {
        Member member = authCommandService.getMember();
        Post post = BoardConverter.toPost(request, member);
        List<PostHashtag> postHashtags = new ArrayList<>();
        // TODO: 유저가 동시에 같은 해시태그 값을 저장하면?
        if (request.getHashtagList() != null) {
            Set<String> uniqueHashtags = new HashSet<>(request.getHashtagList());
            for (String tag : uniqueHashtags) {
                Hashtag hashtag = hashtagRepository.findByNameAndStatus(tag, Status.ACTIVE);
                if (hashtag == null) {
                    hashtag = hashtagRepository.save(BoardConverter.toHashtag(tag));
                }
                PostHashtag postHashtag = BoardConverter.toPostHashtag(post, hashtag);
                postHashtags.add(postHashtag);
            }
        }
        postRepository.save(post);
        postHashtagRepository.saveAll(postHashtags);
        return post;
    }

    @Override
    @Transactional
    public PostBookmark savePostBookmark(Long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));
        PostBookmark bookmark = postBookmarkRepository.findByPostAndMemberAndStatus(post, member, Status.ACTIVE);

        if (bookmark == null) {
            bookmark = BoardConverter.toSavePostBookmark(post, member);
        }
        bookmark.toggleBookmarked();
        postBookmarkRepository.save(bookmark);
        return bookmark;
    }

    @Override
    @Transactional
    public PostLike savePostLike(long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));
        PostLike like = postLikeRepository.findByPostAndMemberAndStatus(post, member, Status.ACTIVE);

        if (like == null) {
            like = BoardConverter.toPostLike(post, member);
        }
        like.toggleLiked();
        postLikeRepository.save(like);
        return like;
    }

    @Override
    @Transactional
    public Post deletePost(Long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));

        if (post.getMember() != member) throw new BoardHandler(ErrorStatus.POST_MEMBER_NOT_FOUND);

        softDeletePostHashtags(post);
        softDeletePostLikes(post);
        softDeletePostBookmarks(post);
        softDeleteComments(post);

        post.softDelete();
        return postRepository.save(post);
    }

    private void softDeletePostHashtags(Post post) {
        List<PostHashtag> postHashtags = postHashtagRepository.findAllByPostAndStatus(post, Status.ACTIVE);
        if (postHashtags != null) {
            postHashtags.forEach(PostHashtag::softDelete);
            postHashtagRepository.saveAll(postHashtags);
        }
    }

    private void softDeleteCommentLikes(Comment comment) {
        List<CommentLike> commentLikes = commentLikeRepository.findAllByCommentAndStatus(comment, Status.ACTIVE);
        if (commentLikes != null) {
            commentLikes.forEach(CommentLike::softDelete);
            commentLikeRepository.saveAll(commentLikes);
        }
        comment.softDelete();
    }


    private void softDeletePostLikes(Post post) {
        List<PostLike> postLikes = postLikeRepository.findAllByPostAndStatus(post, Status.ACTIVE);
        if (postLikes != null) {
            postLikes.forEach(PostLike::softDelete);
            postLikeRepository.saveAll(postLikes);
        }
    }

    private void softDeletePostBookmarks(Post post) {
        List<PostBookmark> postBookmarks = postBookmarkRepository.findAllByPostAndStatus(post, Status.ACTIVE);
        if (postBookmarks != null) {
            postBookmarks.forEach(PostBookmark::softDelete);
            postBookmarkRepository.saveAll(postBookmarks);
        }
    }

    private void softDeleteComments(Post post) {
        List<Comment> comments = commentRepository.findAllByPostAndStatus(post, Status.ACTIVE);
        if (comments != null) {
            comments.forEach(this::softDeleteCommentLikes);
            commentRepository.saveAll(comments);
        }
    }
}
