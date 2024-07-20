package kr.co.teacherforboss.service.mypageService;

import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import kr.co.teacherforboss.web.dto.MypageResponseDTO;

public interface MypageQueryService {
    MypageResponseDTO.GetAnsweredQuestionsDTO getAnsweredQuestions(Long lastQuestionId, int size);
    BoardResponseDTO.GetPostsDTO getAnsweredPosts(Long lastPostId, int size);
}
