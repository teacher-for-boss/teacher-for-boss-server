package kr.co.teacherforboss.web.controller;

import jakarta.validation.Valid;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.PostLike;
import kr.co.teacherforboss.service.boardService.BoardCommandService;
import kr.co.teacherforboss.service.boardService.BoardQueryService;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
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

    @PostMapping("/boss/posts")
    public ApiResponse<BoardResponseDTO.SavePostDTO> savePost(@RequestBody @Valid BoardRequestDTO.SavePostDTO request){
        Post post = boardCommandService.savePost(request);
        return ApiResponse.onSuccess(BoardConverter.toSavePostDTO(post));
    }

    @GetMapping("/boss/posts/{postId}")
    public ApiResponse<BoardResponseDTO.GetPostDTO> getPost(@PathVariable("postId") Long postId){
        return ApiResponse.onSuccess(boardQueryService.getPost(postId));
    }

    @PostMapping("/board/boss/posts/{postId}/likes")
    public ApiResponse<BoardResponseDTO.SaveLikeDTO> saveLike(@PathVariable("postId") Long postId){
        PostLike like = boardCommandService.saveLike(postId);
        return ApiResponse.onSuccess(BoardConverter.toSaveLikeDTO(like));
    }
}
