package kr.co.teacherforboss.service.mypageService;

import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import kr.co.teacherforboss.web.dto.MypageResponseDTO;

public interface MypageQueryService {
    BoardResponseDTO.GetQuestionsDTO getMyQuestions(Long lastQuestionId, int size);
    MypageResponseDTO.GetAnsweredQuestionsDTO getAnsweredQuestions(Long lastQuestionId, int size);
    BoardResponseDTO.GetPostsDTO getCommentedPosts(Long lastPostId, int size);
}
