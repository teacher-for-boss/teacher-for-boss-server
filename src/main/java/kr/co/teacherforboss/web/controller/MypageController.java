package kr.co.teacherforboss.web.controller;

import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.service.mypageService.MypageQueryService;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import kr.co.teacherforboss.web.dto.MypageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageQueryService mypageQueryService;

    @GetMapping("/board/my-questions")
    public ApiResponse<MypageResponseDTO.GetQuestionInfosDTO> getMyQuestions(@RequestParam(defaultValue = "0") Long lastQuestionId,
                                                                             @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.onSuccess(mypageQueryService.getMyQuestions(lastQuestionId, size));
    }

    @GetMapping("/board/answered-questions")
    public ApiResponse<MypageResponseDTO.GetQuestionInfosDTO> getAnsweredQuestions(@RequestParam(defaultValue = "0") Long lastQuestionId,
                                                                                   @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.onSuccess(mypageQueryService.getAnsweredQuestions(lastQuestionId, size));
    }

    @GetMapping("/board/commented-posts")
    public ApiResponse<BoardResponseDTO.GetPostsDTO> getCommentedPosts(@RequestParam(defaultValue = "0") Long lastPostId,
                                                                      @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.onSuccess(mypageQueryService.getCommentedPosts(lastPostId, size));
    }

    @GetMapping("/board/my-posts")
    public ApiResponse<MypageResponseDTO.GetPostInfosDTO> getMyPosts(@RequestParam(defaultValue = "0") Long lastPostId,
                                                                     @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.onSuccess(mypageQueryService.getMyPosts(lastPostId, size));
    }

    @GetMapping("/board/bookmarked-questions")
    public ApiResponse<MypageResponseDTO.GetQuestionInfosDTO> getBookmarkedQuestions(@RequestParam(defaultValue = "0") Long lastQuestionId,
                                                                                     @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.onSuccess(mypageQueryService.getBookmarkedQuestions(lastQuestionId, size));
    }

    @GetMapping("/board/bookmarked-posts")
    public ApiResponse<MypageResponseDTO.GetPostInfosDTO> getBookmarkedPosts(@RequestParam(defaultValue = "0") Long lastPostId,
                                                                             @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.onSuccess(mypageQueryService.getBookmarkedPosts(lastPostId, size));
    }
}
