package kr.co.teacherforboss.service.commentService;

import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.web.dto.CommentRequestDTO;

public interface CommentCommandService {
    Comment saveComment(CommentRequestDTO.SaveCommentDTO request, Long postId);
}
