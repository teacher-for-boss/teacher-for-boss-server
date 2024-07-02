package kr.co.teacherforboss.web.controller;

import kr.co.teacherforboss.domain.AnswerLike;
import kr.co.teacherforboss.domain.CommentLike;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionBookmark;
import kr.co.teacherforboss.domain.QuestionLike;
import kr.co.teacherforboss.service.boardService.BoardCommandService;
import kr.co.teacherforboss.service.boardService.BoardQueryService;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;


@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardCommandService boardCommandService;
    private final BoardQueryService boardQueryService;

    @PostMapping("/boss/posts")
    public ApiResponse<BoardResponseDTO.SavePostDTO> savePost(@RequestBody @Valid BoardRequestDTO.SavePostDTO request){
        Post post = boardCommandService.savePost(request);
        return ApiResponse.onSuccess(BoardConverter.toSavePostDTO(post));
    }

    @GetMapping("/boss/posts/{postId}")
    public ApiResponse<BoardResponseDTO.GetPostDTO> getPost(@PathVariable("postId") Long postId){
        return ApiResponse.onSuccess(boardQueryService.getPost(postId));
    }

    @GetMapping("/boss/posts")
    public ApiResponse<BoardResponseDTO.GetPostsDTO> getPosts(@RequestParam(defaultValue = "0") Long lastPostId,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(defaultValue = "latest") String sortBy){
        return ApiResponse.onSuccess(boardQueryService.getPosts(lastPostId, size, sortBy));
    }

    @PatchMapping("/boss/posts/{postId}")
    public ApiResponse<BoardResponseDTO.SavePostDTO> editPost(@PathVariable("postId") Long postId,
                                                              @RequestBody @Valid BoardRequestDTO.SavePostDTO request) {
        Post post = boardCommandService.editPost(postId, request);
        return ApiResponse.onSuccess(BoardConverter.toSavePostDTO(post));
    }

    @PostMapping("/teacher/questions")
    public ApiResponse<BoardResponseDTO.SaveQuestionDTO> saveQuestion(@RequestBody @Valid BoardRequestDTO.SaveQuestionDTO request) {
        Question question = boardCommandService.saveQuestion(request);
        return ApiResponse.onSuccess(BoardConverter.toSaveQuestionDTO(question));
    }

    @PostMapping("/boss/posts/{postId}/bookmark")
    public ApiResponse<BoardResponseDTO.TogglePostBookmarkDTO> togglePostBookmark(@PathVariable("postId") Long postId){
        PostBookmark bookmark = boardCommandService.togglePostBookmark(postId);
        return ApiResponse.onSuccess(BoardConverter.toTogglePostBookmarkDTO(bookmark));
    }

    @PostMapping("/boss/posts/{postId}/likes")
    public ApiResponse<BoardResponseDTO.TogglePostLikeDTO> togglePostLike(@PathVariable("postId") Long postId){
        PostLike like = boardCommandService.togglePostLike(postId);
        return ApiResponse.onSuccess(BoardConverter.toTogglePostLikeDTO(like));
    }

    @DeleteMapping("/boss/posts/{postId}")
    public ApiResponse<BoardResponseDTO.DeletePostDTO> deletePost(@PathVariable("postId") Long postId){
        Post post = boardCommandService.deletePost(postId);
        return ApiResponse.onSuccess(BoardConverter.toDeletePostDTO(post));
    }

    @PatchMapping("/teacher/questions/{questionId}")
    public ApiResponse<BoardResponseDTO.EditQuestionDTO> editQuestion(@PathVariable("questionId") Long questionId, @RequestBody @Valid BoardRequestDTO.EditQuestionDTO request) {
        Question question = boardCommandService.editQuestion(questionId, request);
        return ApiResponse.onSuccess(BoardConverter.toEditQuestionDTO(question));
    }

    @PostMapping("/teacher/questions/{questionId}/answers")
    public ApiResponse<BoardResponseDTO.SaveAnswerDTO> saveAnswer(@PathVariable("questionId") Long questionId,
                                                                  @RequestBody @Valid BoardRequestDTO.SaveAnswerDTO request) {
        Answer answer = boardCommandService.saveAnswer(questionId, request);
        return ApiResponse.onSuccess(BoardConverter.toSaveAnswerDTO(answer));
    }

    @DeleteMapping("/teacher/questions/{questionId}")
    public ApiResponse<BoardResponseDTO.DeleteQuestionDTO> deleteQuestion(@PathVariable("questionId") Long questionId) {
        Question question = boardCommandService.deleteQuestion(questionId);
        return ApiResponse.onSuccess(BoardConverter.toDeleteQuestionDTO(question));
    }

    @PostMapping("/teacher/questions/{questionId}/likes")
    public ApiResponse<BoardResponseDTO.ToggleQuestionLikeDTO> toggleQuestionLike(@PathVariable("questionId") Long questionId) {
        QuestionLike questionLike = boardCommandService.toggleQuestionLike(questionId);
        return ApiResponse.onSuccess(BoardConverter.toToggleQuestionLikeDTO(questionLike));
    }

    @PatchMapping("/teacher/questions/{questionId}/answers/{answerId}")
    public ApiResponse<BoardResponseDTO.EditAnswerDTO> editAnswer(@PathVariable("questionId") Long questionId,
                                                                  @PathVariable("answerId") Long answerId,
                                                                  @RequestBody @Valid BoardRequestDTO.EditAnswerDTO request) {
        Answer answer = boardCommandService.editAnswer(questionId, answerId, request);
        return ApiResponse.onSuccess(BoardConverter.toEditAnswerDTO(answer));
    }

    @PostMapping("/teacher/questions/{questionId}/answers/{answerId}")
    public ApiResponse<BoardResponseDTO.DeleteAnswerDTO> deleteAnswer(@PathVariable("questionId") Long questionId,
                                                                      @PathVariable("answerId") Long answerId) {
        Answer answer = boardCommandService.deleteAnswer(questionId, answerId);
        return ApiResponse.onSuccess(BoardConverter.toDeleteAnswerDTO(answer));
    }

    @PostMapping("/teacher/questions/{questionId}/bookmark")
    public ApiResponse<BoardResponseDTO.ToggleQuestionBookmarkDTO> toggleQuestionBookmark(@PathVariable("questionId") Long questionId) {
        QuestionBookmark questionBookmark = boardCommandService.toggleQuestionBookmark(questionId);
        return ApiResponse.onSuccess(BoardConverter.toToggleQuestionBookmarkDTO(questionBookmark));
    }

    @GetMapping("/teacher/questions/{questionId}")
    public ApiResponse<BoardResponseDTO.GetQuestionDTO> getQuestion(@PathVariable("questionId") Long questionId){
        return ApiResponse.onSuccess(boardQueryService.getQuestion(questionId));
    }

    @GetMapping("/teacher/questions/{questionId}/answers")
    public ApiResponse<BoardResponseDTO.GetAnswersDTO> getAnswers(@PathVariable("questionId") Long questionId,
                                                                  @RequestParam(name = "lastAnswerId", defaultValue = "0") Long lastAnswerId,
                                                                  @RequestParam(name = "size", defaultValue = "10") int size) {
        return ApiResponse.onSuccess(boardQueryService.getAnswers(questionId, lastAnswerId, size));
    }

    @GetMapping("/boss/posts/{postId}/comments")
    public ApiResponse<BoardResponseDTO.GetCommentsDTO> getComments(@PathVariable("postId") Long postId,
                                                                    @RequestParam(name = "lastCommentId", defaultValue = "0") Long lastCommentId,
                                                                    @RequestParam(name = "size", defaultValue = "10") int size) {
        return ApiResponse.onSuccess(boardQueryService.getComments(postId, lastCommentId, size));
    }

    @PostMapping("/boss/posts/{postId}/comments")
    public ApiResponse<BoardResponseDTO.SaveCommentDTO> saveComment(@PathVariable("postId") Long postId,
                                                                   @RequestBody @Valid BoardRequestDTO.SaveCommentDTO request) {
        Comment comment = boardCommandService.saveComment(postId, request);
        return ApiResponse.onSuccess(BoardConverter.toSaveCommentDTO(comment));
    }

    @PostMapping("/teacher/questions/{questionId}/answers/{answerId}/likes")
    public ApiResponse<BoardResponseDTO.ToggleAnswerLikeDTO> toggleAnswerLike(@PathVariable("questionId") Long questionId,
                                                                       @PathVariable("answerId") Long answerId) {
        AnswerLike answerLike = boardCommandService.toggleAnswerLike(questionId, answerId, true);
        return ApiResponse.onSuccess(BoardConverter.toToggleAnswerLikeDTO(answerLike));
    }

    @PostMapping("/teacher/questions/{questionId}/answers/{answerId}/dislikes")
    public ApiResponse<BoardResponseDTO.ToggleAnswerLikeDTO> toggleAnswerDislike(@PathVariable("questionId") Long questionId,
                                                                        @PathVariable("answerId") Long answerId) {
        AnswerLike answerLike = boardCommandService.toggleAnswerLike(questionId, answerId, false);
        return ApiResponse.onSuccess(BoardConverter.toToggleAnswerLikeDTO(answerLike));
    }

    @PostMapping("/boss/posts/{postId}/comments/{commentId}/likes")
    public ApiResponse<BoardResponseDTO.ToggleCommentLikeDTO> saveCommentLike(@PathVariable("postId") Long postId,
                                                                              @PathVariable("commentId") Long commentId) {
        CommentLike commentLike = boardCommandService.toggleCommentLike(postId, commentId, true);
        return ApiResponse.onSuccess(BoardConverter.toToggleCommentLikeDTO(commentLike));
    }

    @PostMapping("/boss/posts/{postId}/comments/{commentId}/dislikes")
    public ApiResponse<BoardResponseDTO.ToggleCommentLikeDTO> saveCommentDisLike(@PathVariable("postId") Long postId,
                                                                                 @PathVariable("commentId") Long commentId) {
        CommentLike commentLike = boardCommandService.toggleCommentLike(postId, commentId, false);
        return ApiResponse.onSuccess(BoardConverter.toToggleCommentLikeDTO(commentLike));
    }

    @PatchMapping("/teacher/questions/{questionId}/answers/{answerId}/select")
    public ApiResponse<BoardResponseDTO.SelectAnswerDTO> selectAnswer(@PathVariable("questionId") Long questionId,
                                                                   @PathVariable("answerId") Long answerId) {
        Answer answer = boardCommandService.selectAnswer(questionId, answerId);
        return ApiResponse.onSuccess(BoardConverter.toSelectAnswerDTO(answer));
    }
}
