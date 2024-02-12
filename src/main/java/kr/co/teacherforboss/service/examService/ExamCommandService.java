package kr.co.teacherforboss.service.examService;

import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.web.dto.ExamRequestDTO;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;

public interface ExamCommandService {
    MemberExam takeExams(Long examId, ExamRequestDTO.TakeExamsDTO request);

    ExamResponseDTO.GetExamResultDTO getExamResult(Long examId);
}