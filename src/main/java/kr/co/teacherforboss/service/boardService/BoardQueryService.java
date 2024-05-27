package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;

public interface BoardQueryService {
    BoardResponseDTO.GetPostDTO getPost(Long postId);
    BoardResponseDTO.GetPresignedUrlDTO getPresignedUrl(BoardRequestDTO.GetPresignedUrlDTO request);
}
