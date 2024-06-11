package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.web.dto.BoardResponseDTO;

public interface BoardQueryService {
    BoardResponseDTO.GetPostDTO getPost(Long postId);
    BoardResponseDTO.GetAnswersDTO getAnswers(Long questionId);
}
