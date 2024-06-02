package kr.co.teacherforboss.service.boardService;

import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionBookmark;
import kr.co.teacherforboss.domain.QuestionLike;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;

public interface BoardCommandService {
    Post savePost(BoardRequestDTO.SavePostDTO request);
    Question saveQuestion(BoardRequestDTO.SaveQuestionDTO request);
    PostBookmark savePostBookmark(Long postId);
    PostLike savePostLike(long postId);
    Question editQuestion(Long questionId, BoardRequestDTO.EditQuestionDTO request);
    Question deleteQuestion(Long questionId);
    QuestionLike likeQuestion(Long questionId);
    QuestionBookmark bookmarkQuestion(Long questionId);
}
