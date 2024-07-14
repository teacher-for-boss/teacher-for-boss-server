package kr.co.teacherforboss.service.mypageService;

import kr.co.teacherforboss.web.dto.BoardResponseDTO;

public interface MypageQueryService {
    BoardResponseDTO.GetQuestionsDTO getMyQuestions(Long lastQuestionId, int size);
}
