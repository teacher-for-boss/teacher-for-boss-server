package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.web.dto.BoardResponseDTO;

public interface BoardQueryService {
    BoardResponseDTO.GetPostDTO getPost(Long postId);
    BoardResponseDTO.GetPostsDTO getPosts(Long lastPostId, int size, String sortBy);
    BoardResponseDTO.GetQuestionDTO getQuestion(Long questionId);
    BoardResponseDTO.GetAnswersDTO getAnswers(Long questionId, Long lastAnswerId, int size);
    BoardResponseDTO.GetCommentListDTO getComments(Long postId);

}
