package kr.co.teacherforboss.service.mypageService;

import kr.co.teacherforboss.web.dto.MypageResponseDTO;

public interface MypageQueryService {
    MypageResponseDTO.GetAnsweredQuestionsDTO getAnsweredQuestions(Long lastQuestionId, int size);
}
