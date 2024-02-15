package kr.co.teacherforboss.service.examService;

import java.util.List;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.ExamType;

public interface ExamQueryService {
    List<ExamCategory> getExamCategories();
    List<Question> getQuestions(Long examId, ExamType examType);
}
