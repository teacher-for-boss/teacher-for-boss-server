package kr.co.teacherforboss.web.controller;

import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.scheduler.HomeScheduler;
import kr.co.teacherforboss.service.homeService.HomeQueryService;
import kr.co.teacherforboss.web.dto.HomeResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeQueryService homeQueryService;
    private final HomeScheduler homeScheduler;

    @GetMapping("/hot-posts")
    public ApiResponse<HomeResponseDTO.GetHotPostsDTO> getHotPosts() {
        return ApiResponse.onSuccess(homeQueryService.getHotPosts());
    }

    @GetMapping("/hot-questions")
    public ApiResponse<HomeResponseDTO.GetHotQuestionsDTO> getHotQuestions() {
        return ApiResponse.onSuccess(homeQueryService.getHotQuestions());
    }

    @GetMapping("/hot-teachers")
    public ApiResponse<HomeResponseDTO.GetHotTeachersDTO> getHotTeachers() {
        return ApiResponse.onSuccess(homeQueryService.getHotTeachers());
    }

    @PostMapping("/hot-posts/refresh")
    public ApiResponse<HomeResponseDTO.GetHotPostsDTO> refreshHotPosts() {
        return ApiResponse.onSuccess(homeScheduler.updateHotPosts());
    }

    @PostMapping("/hot-questions/refresh")
    public ApiResponse<HomeResponseDTO.GetHotQuestionsDTO> refreshHotQuestions() {
        return ApiResponse.onSuccess(homeScheduler.updateHotQuestions());
    }

    @PostMapping("/hot-teachers/refresh")
    public ApiResponse<HomeResponseDTO.GetHotTeachersDTO> refreshHotTeachers() {
        return ApiResponse.onSuccess(homeScheduler.updateHotTeachers());
    }
}
