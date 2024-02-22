package kr.co.teacherforboss.service.examService;

import kr.co.teacherforboss.domain.MemberAnswer;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.web.dto.ExamRequestDTO;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;

import java.util.List;

public interface ExamCommandService {
    MemberExam takeExams(Long examId, ExamRequestDTO.TakeExamsDTO request);

    ExamResponseDTO.GetExamResultDTO getExamResult(Long examId);

    List<Question> getExamAnsNotes(Long examId);
}