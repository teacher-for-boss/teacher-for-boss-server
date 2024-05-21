package kr.co.teacherforboss.service.examService;

import java.util.List;

import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Problem;
import kr.co.teacherforboss.domain.ExamTag;
import kr.co.teacherforboss.domain.enums.ExamQuarter;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;

public interface ExamQueryService {
    List<ExamCategory> getExamCategories();
    List<ExamTag> getExamTags(Long categoryId);
    List<Exam> getExams(Long examCategoryId, Long examTagId);
    List<MemberExam> getMemberExams();
    List<Problem> getProblems(Long examId);
    List<ExamResponseDTO.GetExamRankInfoDTO.ExamRankInfo> getExamRankInfo(Long examId);
    ExamResponseDTO.GetAverageDTO getAverage(ExamQuarter examQuarter);
    List<Exam> getTakenExams();
}
