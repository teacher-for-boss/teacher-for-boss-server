package kr.co.teacherforboss.service.examService;

import java.util.List;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.web.dto.ExamRequestDTO;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;

public interface ExamCommandService {
    MemberExam takeExam(Long examId, ExamRequestDTO.TakeExamDTO request);

    ExamResponseDTO.GetExamResultDTO getExamResult(Long examId);

    List<Question> getExamIncorrectAnswers(Long examId);
}