package kr.co.teacherforboss.service.commentService;

import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;

public interface CommentCommandService {
    Comment saveComment(Long postId, BoardRequestDTO.SaveCommentDTO request);
}
