package kr.co.teacherforboss.service.examService;

import java.util.List;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.Tag;
import kr.co.teacherforboss.domain.enums.ExamType;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;

public interface ExamQueryService {
    List<ExamCategory> getExamCategories();
    List<Tag> getTags();
    List<Question> getQuestions(Long examId, ExamType examType);
    List<ExamResponseDTO.GetExamRankInfoDTO.ExamRankInfo> getExamRankInfo(Long examId);
}
