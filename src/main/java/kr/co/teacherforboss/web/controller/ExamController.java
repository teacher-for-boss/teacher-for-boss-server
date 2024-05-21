package kr.co.teacherforboss.web.controller;

import jakarta.validation.Valid;
import java.util.List;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.config.ExamConfig;
import kr.co.teacherforboss.converter.ExamConverter;
import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Problem;
import kr.co.teacherforboss.domain.ExamTag;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    public ApiResponse<ExamResponseDTO.TakeExamDTO> takeExam(@PathVariable("examId") Long examId, @RequestBody @Valid ExamRequestDTO.TakeExamDTO request) {
        MemberExam memberExam = examCommandService.takeExam(examId, request);
        return ApiResponse.onSuccess(ExamConverter.toTakeExamDTO(memberExam));
    }

    @GetMapping(" /member-exams/{memberExamId}/result")
    public ApiResponse<ExamResponseDTO.GetExamResultDTO> getExamResult(@PathVariable("memberExamId") Long memberExamId) {
        return ApiResponse.onSuccess(examCommandService.getExamResult(memberExamId));
    }
  
    @GetMapping("/category")
    public ApiResponse<ExamResponseDTO.GetExamCategoriesDTO> getExamCategories() {
        List<ExamCategory> examCategories = examQueryService.getExamCategories();
        return ApiResponse.onSuccess(ExamConverter.toGetExamCategoriesDTO(examCategories));
    }

    @GetMapping("/{examCategoryId}/exam-tags")
    public ApiResponse<ExamResponseDTO.GetExamTagsDTO> getExamTags(@PathVariable("examCategoryId") Long examCategoryId) {
        List<ExamTag> examTags = examQueryService.getExamTags(examCategoryId);
        return ApiResponse.onSuccess(ExamConverter.toGetExamTagsDTO(examTags));
    }

    @GetMapping("/list/{examCategoryId}")
    public ApiResponse<ExamResponseDTO.GetExamsDTO> getExams(@PathVariable("examCategoryId") Long examCategoryId,
                                                             @RequestParam(value = "examTagId", required = false) Long examTagId) {
        List<Exam> exams = examQueryService.getExams(examCategoryId, examTagId);
        List<MemberExam> memberExams = examQueryService.getMemberExams();
        return ApiResponse.onSuccess(ExamConverter.toGetExamsDTO(exams, memberExams));
    }

    @GetMapping("/member-exams/{memberExamId}/result/incorrect/list")
    public ApiResponse<ExamResponseDTO.GetExamIncorrectChoicesResultDTO> getExamIncorrectChoices(@PathVariable("memberExamId") Long memberExamId) {
        List<Problem> problems = examCommandService.getExamIncorrectChoices(memberExamId);
        return ApiResponse.onSuccess(ExamConverter.toGetExamAnsNotesDTO(problems));
    }

    @GetMapping("/{examId}")
    public ApiResponse<ExamResponseDTO.GetProblemsDTO> getProblems(@PathVariable("examId") Long examId) {
        List<Problem> problems = examQueryService.getProblems(examId);
        return ApiResponse.onSuccess(ExamConverter.toGetProblemsDTO(problems));
    }

    @GetMapping("/{examId}/solutions")
    public ApiResponse<ExamResponseDTO.GetSolutionsDTO> getSolutions(@PathVariable("examId") Long examId) {
        List<Problem> problems = examQueryService.getProblems(examId);
        return ApiResponse.onSuccess(ExamConverter.toGetSolutionsDTO(problems));
    }
    
    @GetMapping("/{examId}/rank")
    public ApiResponse<ExamResponseDTO.GetExamRankInfoDTO> getExamRankInfo(@PathVariable("examId") Long examId) {
        List<ExamResponseDTO.GetExamRankInfoDTO.ExamRankInfo> rankInfos = examQueryService.getExamRankInfo(examId);
        return ApiResponse.onSuccess(ExamConverter.toGetExamRankInfoDTO(rankInfos));
    }

    @GetMapping("/average")
    public ApiResponse<ExamResponseDTO.GetAverageDTO> getAverage() {
        return ApiResponse.onSuccess(examQueryService.getAverage(ExamConfig.EXAM_QUARTER));
    }
  
    @GetMapping("/count")
    public ApiResponse<ExamResponseDTO.GetTakenExamCountDTO> getTakenExamCount() {
        List<Exam> takenExams = examQueryService.getTakenExams();
        return ApiResponse.onSuccess(ExamConverter.toGetTakenExamCountDTO(takenExams));
    }
}
