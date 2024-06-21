package kr.co.teacherforboss.service.commentService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.CommentConverter;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.CommentLike;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.CommentLikeRepository;
import kr.co.teacherforboss.repository.CommentRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.CommentRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCommandServiceImpl implements CommentCommandService {
    private final AuthCommandService authCommandService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    @Transactional
    public Comment saveComment(Long postId, CommentRequestDTO.SaveCommentDTO request) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));

        Comment parentComment = null;
        if(request.getParentId() != null) parentComment = commentRepository.findByIdAndStatus(request.getParentId(), Status.ACTIVE);

        Comment comment = CommentConverter.toCommentDTO(request, member, post, parentComment);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public CommentLike saveCommentLike(Long postId, Long commentId) {
        return saveCommentLikeOrDislike(postId, commentId, BooleanType.T);
    }

    @Override
    @Transactional
    public CommentLike saveCommentDislike(Long postId, Long commentId) {
        return saveCommentLikeOrDislike(postId, commentId, BooleanType.F);
    }

    private CommentLike saveCommentLikeOrDislike(Long postId, Long commentId, BooleanType type) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));
        Comment comment = commentRepository.findByIdAndPostAndStatus(commentId, post, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.COMMENT_NOT_FOUND));

        CommentLike commentLike = commentLikeRepository.findByCommentAndMemberAndStatus(comment, member, Status.ACTIVE);
        BooleanType preLikeType = commentLike != null ? commentLike.getLiked() : null;

        if (commentLike == null) {
            commentLike = CommentConverter.toCommentLiked(comment, member, type);
        } else if (type == BooleanType.T) {
            commentLike.setLiked();
        } else {
            commentLike.setDisliked();
        }

        updateLikeOrDislikeCount(comment, preLikeType, commentLike.getLiked());
        return commentLikeRepository.save(commentLike);
    }

    private void updateLikeOrDislikeCount(Comment comment, BooleanType preLikeType, BooleanType nowLikeType) {
        if (preLikeType == BooleanType.T) {
            if (nowLikeType == BooleanType.F) {
                comment.setLikeCount(false);
                comment.setDislikeCount(true);
            } else if (nowLikeType == null) {
                comment.setLikeCount(false);
            }
        } else if (preLikeType == BooleanType.F) {
            if (nowLikeType == BooleanType.T) {
                comment.setDislikeCount(false);
                comment.setLikeCount(true);
            } else if (nowLikeType == null) {
                comment.setDislikeCount(false);
            }
        } else if (preLikeType == null) {
            if (nowLikeType == BooleanType.F) {
                comment.setDislikeCount(true);
            } else if (nowLikeType == BooleanType.T) {
                comment.setLikeCount(true);
            }
        }
    }
}
