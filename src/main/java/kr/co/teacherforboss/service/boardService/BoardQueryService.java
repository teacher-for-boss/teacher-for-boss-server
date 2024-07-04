package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.web.dto.BoardResponseDTO;

public interface BoardQueryService {
    BoardResponseDTO.GetPostDTO getPost(Long postId);
    BoardResponseDTO.GetPostsDTO getPosts(Long lastPostId, int size, String sortBy);
    BoardResponseDTO.GetQuestionDTO getQuestion(Long questionId);
    BoardResponseDTO.GetAnswersDTO getAnswers(Long questionId, Long lastAnswerId, int size);
    BoardResponseDTO.GetCommentsDTO getComments(Long postId, Long lastCommentId, int size);
    BoardResponseDTO.GetQuestionsDTO getQuestions(Long lastQuestionId, int size, String sortBy, String categoryId);
    BoardResponseDTO.GetPostsDTO searchPosts(String keyword, Long lastPostId, int size, String sortBy);
}
