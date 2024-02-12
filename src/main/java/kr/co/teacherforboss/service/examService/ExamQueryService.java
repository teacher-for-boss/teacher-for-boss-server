package kr.co.teacherforboss.service.examService;

import java.util.List;
import kr.co.teacherforboss.domain.ExamCategory;

public interface ExamQueryService {
    List<ExamCategory> getExamCategories();
}
