package kr.co.teacherforboss.service.examService;

import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.web.dto.ExamRequestDTO;

public interface ExamCommandService {
    MemberExam takeExams(Long examId, ExamRequestDTO.TakeExamsDTO request);
}