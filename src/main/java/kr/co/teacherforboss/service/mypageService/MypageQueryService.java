package kr.co.teacherforboss.service.mypageService;

import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import kr.co.teacherforboss.web.dto.MypageResponseDTO;

public interface MypageQueryService {
    MypageResponseDTO.GetQuestionInfosDTO getMyQuestions(Long lastQuestionId, int size);
    MypageResponseDTO.GetQuestionInfosDTO getAnsweredQuestions(Long lastQuestionId, int size);
    BoardResponseDTO.GetPostsDTO getCommentedPosts(Long lastPostId, int size);
    MypageResponseDTO.GetPostInfosDTO getMyPosts(Long lastPostId, int size);
    MypageResponseDTO.GetQuestionInfosDTO getBookmarkedQuestions(Long lastQuestionId, int size);
    MypageResponseDTO.GetPostInfosDTO getBookmarkedPosts(Long lastPostId, int size);
    MypageResponseDTO.GetChipInfoDTO getChipInfo();
}
