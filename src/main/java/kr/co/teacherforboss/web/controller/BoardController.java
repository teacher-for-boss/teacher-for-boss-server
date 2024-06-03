package kr.co.teacherforboss.web.controller;

import jakarta.validation.Valid;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.converter.CommentConverter;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.CommentLike;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.service.boardService.BoardCommandService;
import kr.co.teacherforboss.service.boardService.BoardQueryService;
import kr.co.teacherforboss.service.commentService.CommentCommandService;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import kr.co.teacherforboss.web.dto.CommentRequestDTO;
import kr.co.teacherforboss.web.dto.CommentResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardCommandService boardCommandService;
    private final BoardQueryService boardQueryService;
    private final CommentCommandService commentCommandService;

    @PostMapping("/boss/posts")
    public ApiResponse<BoardResponseDTO.SavePostDTO> savePost(@RequestBody @Valid BoardRequestDTO.SavePostDTO request){
        Post post = boardCommandService.savePost(request);
        return ApiResponse.onSuccess(BoardConverter.toSavePostDTO(post));
    }

    @GetMapping("/boss/posts/{postId}")
    public ApiResponse<BoardResponseDTO.GetPostDTO> getPost(@PathVariable("postId") Long postId){
        return ApiResponse.onSuccess(boardQueryService.getPost(postId));
    }

    @PostMapping("/boss/posts/{postId}")
    public ApiResponse<BoardResponseDTO.SavePostDTO> modifyPost(@PathVariable("postId") Long postId,
                                                                @RequestBody @Valid BoardRequestDTO.SavePostDTO request) {
        Post post = boardCommandService.modifyPost(postId, request);
        return ApiResponse.onSuccess(BoardConverter.toSavePostDTO(post));
    }

    @PostMapping("/boss/posts/{postId}/comments")
    public ApiResponse<CommentResponseDTO.SaveCommentResultDTO> saveComment(@PathVariable("postId") Long postId,
                                                                            @RequestBody @Valid CommentRequestDTO.SaveCommentDTO request) {
        Comment comment = commentCommandService.saveComment(request, postId);
        return ApiResponse.onSuccess(CommentConverter.toSaveCommentResultDTO(comment));
    }

    @PostMapping("boss/posts/{postId}/comments/{commentId}/likes")
    public ApiResponse<CommentResponseDTO.SaveCommentLikedResultDTO> saveCommentLike(@PathVariable("postId") Long postId,
                                                                                     @PathVariable("commentId") Long commentsId) {
        CommentLike commentLike = commentCommandService.saveCommentLike(postId, commentsId);
        return ApiResponse.onSuccess(CommentConverter.toSaveCommentLikedResultDTO(commentLike));
    }
}
