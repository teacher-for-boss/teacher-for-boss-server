package kr.co.teacherforboss.web.controller;

import jakarta.validation.Valid;
import java.util.List;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.config.ExamConfig;
import kr.co.teacherforboss.converter.ExamConverter;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.service.examService.ExamCommandService;
import kr.co.teacherforboss.service.examService.ExamQueryService;
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
    private final ExamQueryService examQueryService;

    @PostMapping("/{examId}")
    public ApiResponse<ExamResponseDTO.TakeExamsDTO> takeExam(@PathVariable("examId") Long examId, @RequestBody @Valid ExamRequestDTO.TakeExamsDTO request) {
        MemberExam memberExam = examCommandService.takeExams(examId, request);
        return ApiResponse.onSuccess(ExamConverter.toTakeExamsDTO(memberExam));
    }

    @GetMapping("/{examId}/result")
    public ApiResponse<ExamResponseDTO.GetExamResultDTO> getExamResult(@PathVariable("examId") Long examId) {
        return ApiResponse.onSuccess(examCommandService.getExamResult(examId));
    }
  
    @GetMapping("/category")
    public ApiResponse<ExamResponseDTO.GetExamCategoriesDTO> getExamCategories() {
        List<ExamCategory> examCategories = examQueryService.getExamCategories();
        return ApiResponse.onSuccess(ExamConverter.toGetExamCategoriesDTO(examCategories));
    }

    @GetMapping("/{examId}/result/incorrect/list")
    public ApiResponse<ExamResponseDTO.GetExamIncorrectAnswersResultDTO> getExamIncorrectAnswers(@PathVariable("examId") Long examId) {
        List<Question> questions = examCommandService.getExamIncorrectAnswers(examId);
        return ApiResponse.onSuccess(ExamConverter.toGetExamAnsNotesDTO(questions));
    }

    @GetMapping("/{examId}")
    public ApiResponse<ExamResponseDTO.GetQuestionsDTO> getQuestions(@PathVariable("examId") Long examId) {
        List<Question> questions = examQueryService.getQuestions(examId, ExamConfig.EXAM_TYPE);
        return ApiResponse.onSuccess(ExamConverter.toGetQuestionsDTO(questions));
    }

    @GetMapping("/{examId}/rank")
    public ApiResponse<ExamResponseDTO.GetExamRankInfoDTO> getExamRankInfo(@PathVariable("examId") Long examId) {
        List<ExamResponseDTO.GetExamRankInfoDTO.ExamRankInfo> rankInfos = examQueryService.getExamRankInfo(examId);
        return ApiResponse.onSuccess(ExamConverter.toGetExamRankInfoDTO(rankInfos));
    }
}
