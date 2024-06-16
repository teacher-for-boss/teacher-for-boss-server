package kr.co.teacherforboss.web.controller;

import kr.co.teacherforboss.domain.Answer;
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
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.PostBookmark;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.service.boardService.BoardCommandService;
import kr.co.teacherforboss.service.boardService.BoardQueryService;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

}
