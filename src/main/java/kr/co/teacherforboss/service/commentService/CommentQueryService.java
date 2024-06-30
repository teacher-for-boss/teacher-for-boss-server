package kr.co.teacherforboss.service.commentService;

import kr.co.teacherforboss.web.dto.BoardResponseDTO;

public interface CommentQueryService {
    BoardResponseDTO.GetCommentListDTO getCommentListDTO(Long postId);
}
