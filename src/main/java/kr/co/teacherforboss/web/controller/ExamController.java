package kr.co.teacherforboss.web.controller;

import jakarta.validation.Valid;
import java.util.List;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.ExamConverter;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.service.examService.ExamCommandService;
import kr.co.teacherforboss.web.dto.ExamRequestDTO;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;
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
@RequestMapping("api/v1/exams")
@RequiredArgsConstructor
public class ExamController {
    private final ExamCommandService examCommandService;

    @PostMapping("/{examId}")
    public ApiResponse<ExamResponseDTO.TakeExamsDTO> takeExams(@PathVariable("examId") Long examId, @RequestBody @Valid ExamRequestDTO.TakeExamsDTO request) {
        MemberExam memberExam = examCommandService.takeExams(examId, request);
        return ApiResponse.onSuccess(ExamConverter.toTakeExamsDTO(memberExam));
    }

    @GetMapping("/category")
    public ApiResponse<ExamResponseDTO.GetExamCategoriesDTO> getExamCategories() {
        List<ExamCategory> examCategories = examCommandService.getExamCategories();
        return ApiResponse.onSuccess(ExamConverter.toGetExamCategoriesDTO(examCategories));
    }
}
