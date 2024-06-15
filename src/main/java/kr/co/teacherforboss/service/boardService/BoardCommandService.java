package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;

public interface BoardCommandService {
    Post savePost(BoardRequestDTO.SavePostDTO request);
    Question saveQuestion(BoardRequestDTO.SaveQuestionDTO request);
    PostBookmark savePostBookmark(Long postId);
    PostLike savePostLike(long postId);
    Question editQuestion(Long questionId, BoardRequestDTO.EditQuestionDTO request);
    Answer saveAnswer(long questionId, BoardRequestDTO.SaveAnswerDTO request);
    Answer editAnswer(Long questionId, Long answerId, BoardRequestDTO.EditAnswerDTO request);
    Question deleteQuestion(Long questionId);
}
