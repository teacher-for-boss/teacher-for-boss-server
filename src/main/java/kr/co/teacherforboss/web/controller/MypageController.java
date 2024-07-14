package kr.co.teacherforboss.web.controller;

import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.service.mypageService.MypageQueryService;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
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
    public ApiResponse<BoardResponseDTO.GetQuestionsDTO> getMyQuestions(@RequestParam(defaultValue = "0") Long lastQuestionId,
                                                                        @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.onSuccess(mypageQueryService.getMyQuestions(lastQuestionId, size));
    }
}
