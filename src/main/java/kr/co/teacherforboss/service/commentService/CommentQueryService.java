package kr.co.teacherforboss.service.commentService;

import kr.co.teacherforboss.web.dto.CommentResponseDTO;

public interface CommentQueryService {
    CommentResponseDTO.GetCommentListDTO getCommentListDTO(Long postId);
}
