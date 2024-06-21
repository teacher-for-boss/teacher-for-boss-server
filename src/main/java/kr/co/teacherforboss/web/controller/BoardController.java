package kr.co.teacherforboss.web.controller;

import kr.co.teacherforboss.converter.CommentConverter;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.CommentLike;
import kr.co.teacherforboss.service.commentService.CommentCommandService;
import kr.co.teacherforboss.service.commentService.CommentQueryService;
import kr.co.teacherforboss.web.dto.CommentRequestDTO;
import kr.co.teacherforboss.web.dto.CommentResponseDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionLike;
import kr.co.teacherforboss.service.boardService.BoardCommandService;
import kr.co.teacherforboss.service.boardService.BoardQueryService;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardCommandService boardCommandService;
    private final BoardQueryService boardQueryService;
    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

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
    public ApiResponse<BoardResponseDTO.GetPostListDTO> getPostList(@RequestParam(defaultValue = "0") Long lastPostId, @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(defaultValue = "latest") String sortBy){
        return ApiResponse.onSuccess(boardQueryService.getPostList(lastPostId, size, sortBy));
    }

    @PostMapping("/boss/posts/{postId}")
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
    public ApiResponse<BoardResponseDTO.SavePostBookmarkDTO> savePostBookmark(@PathVariable("postId") Long postId){
        PostBookmark bookmark = boardCommandService.savePostBookmark(postId);
        return ApiResponse.onSuccess(BoardConverter.toSavePostBookmarkDTO(bookmark));
    }

    @PostMapping("/boss/posts/{postId}/likes")
    public ApiResponse<BoardResponseDTO.SavePostLikeDTO> savePostLike(@PathVariable("postId") Long postId){
        PostLike like = boardCommandService.savePostLike(postId);
        return ApiResponse.onSuccess(BoardConverter.toSavePostLikeDTO(like));
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

    @PostMapping("/teacher/questions/{questionId}")
    public ApiResponse<BoardResponseDTO.DeleteQuestionDTO> deleteQuestion(@PathVariable("questionId") Long questionId) {
        Question question = boardCommandService.deleteQuestion(questionId);
        return ApiResponse.onSuccess(BoardConverter.toDeleteQuestionDTO(question));
    }

    @PostMapping("/teacher/questions/{questionId}/likes")
    public ApiResponse<BoardResponseDTO.LikeQuestionDTO> toggleQuestionLike(@PathVariable("questionId") Long questionId) {
        QuestionLike questionLike = boardCommandService.toggleQuestionLike(questionId);
        return ApiResponse.onSuccess(BoardConverter.toLikeQuestionDTO(questionLike));
    }

    @PatchMapping("/teacher/questions/{questionId}/answers/{answerId}")
    public ApiResponse<BoardResponseDTO.EditAnswerDTO> editAnswer(@PathVariable("questionId") Long questionId,
                                                                  @PathVariable("answerId") Long answerId,
                                                                  @RequestBody @Valid BoardRequestDTO.EditAnswerDTO request) {
        Answer answer = boardCommandService.editAnswer(questionId, answerId, request);
        return ApiResponse.onSuccess(BoardConverter.toEditAnswerDTO(answer));
    }

    @PostMapping("/boss/posts/{postId}/comments")
    public ApiResponse<CommentResponseDTO.SaveCommentDTO> saveComment(@PathVariable("postId") Long postId,
                                                                      @RequestBody @Valid CommentRequestDTO.SaveCommentDTO request) {
        Comment comment = commentCommandService.saveComment(request, postId);
        return ApiResponse.onSuccess(CommentConverter.toSaveCommentDTO(comment));
    }

    @PostMapping("/boss/posts/{postId}/comments/{commentId}/likes")
    public ApiResponse<CommentResponseDTO.SaveCommentLikeDTO> saveCommentLike(@PathVariable("postId") Long postId,
                                                                              @PathVariable("commentId") Long commentId) {
        CommentLike commentLike = commentCommandService.saveCommentLike(postId, commentId);
        return ApiResponse.onSuccess(CommentConverter.toSaveCommentLikeDTO(commentLike));
    }

    @PostMapping("/boss/posts/{postId}/comments/{commentId}/dislikes")
    public ApiResponse<CommentResponseDTO.SaveCommentLikeDTO> saveCommentDisLike(@PathVariable("postId") Long postId,
                                                                                 @PathVariable("commentId") Long commentId) {
        CommentLike commentLike = commentCommandService.saveCommentDislike(postId, commentId);
        return ApiResponse.onSuccess(CommentConverter.toSaveCommentLikeDTO(commentLike));
    }

    @GetMapping("/boss/posts/{postId}/comments")
    public ApiResponse<CommentResponseDTO.GetCommentListDTO> getCommentList(@PathVariable("postId") Long postId) {
        return ApiResponse.onSuccess(commentQueryService.getCommentListDTO(postId));
    }
}
