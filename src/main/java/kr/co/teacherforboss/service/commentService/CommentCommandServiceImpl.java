package kr.co.teacherforboss.service.commentService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.converter.CommentConverter;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.CommentLike;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Post;
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
    public Comment saveComment(CommentRequestDTO.SaveCommentDTO request, Long postId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));

        if(request.getParentId() != null) {
            boolean checkParentId = commentRepository.existsByIdAndStatus(request.getParentId(), Status.ACTIVE);
            if(!checkParentId) throw new BoardHandler(ErrorStatus.COMMENT_NOT_FOUND);
        }

        Comment parentComment = commentRepository.findByIdAndStatus(request.getParentId(), Status.ACTIVE);
        Comment comment = CommentConverter.toCommentDTO(request, member, post, parentComment);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public CommentLike saveCommentLike(Long postId, Long commentId) {
        Member member = authCommandService.getMember();
        Post post = postRepository.findByIdAndStatus(postId, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.POST_NOT_FOUND));
        Comment comment = commentRepository.findByIdAndPostAndStatus(commentId, post, Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.COMMENT_NOT_FOUND));

        CommentLike like = commentLikeRepository.findByCommentAndMemberAndStatus(comment, member, Status.ACTIVE);

        if (like == null) like = CommentConverter.toCommentLike(comment, member);
        like.setLiked();

        return commentLikeRepository.save(like);
    }
}
