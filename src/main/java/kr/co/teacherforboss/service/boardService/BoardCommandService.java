package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionBookmark;
import kr.co.teacherforboss.domain.QuestionLike;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;

public interface BoardCommandService {
    Post savePost(BoardRequestDTO.SavePostDTO request);
    Post editPost(Long postId, BoardRequestDTO.SavePostDTO request);
    Question saveQuestion(BoardRequestDTO.SaveQuestionDTO request);
    PostBookmark savePostBookmark(Long postId);
    PostLike savePostLike(long postId);
    Post deletePost(Long postId);
    PostLike savePostLike(Long postId);
    Question editQuestion(Long questionId, BoardRequestDTO.EditQuestionDTO request);
    Answer saveAnswer(long questionId, BoardRequestDTO.SaveAnswerDTO request);
    Answer editAnswer(Long questionId, Long answerId, BoardRequestDTO.EditAnswerDTO request);
    Question deleteQuestion(Long questionId);
    QuestionLike toggleQuestionLike(Long questionId);
    Answer deleteAnswer(Long questionId, Long answerId);
    QuestionBookmark toggleQuestionBookmark(Long questionId);
}
