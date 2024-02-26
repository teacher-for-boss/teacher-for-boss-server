package kr.co.teacherforboss.service.examService;

import java.util.List;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.ExamHandler;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.ExamType;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.ExamCategoryRepository;
import kr.co.teacherforboss.repository.ExamRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExamQueryServiceImpl implements ExamQueryService {

    private final ExamRepository examRepository;
    private final ExamCategoryRepository examCategoryRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ExamCategory> getExamCategories() {
        return examCategoryRepository.findExamCategoriesByStatus(Status.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> getQuestions(Long examId, ExamType examType) {
        if (!examRepository.existsByIdAndStatus(examId, Status.ACTIVE))
            throw new ExamHandler(ErrorStatus.EXAM_NOT_FOUND);

        return questionRepository.findAllByExamIdAndStatus(examId, Status.ACTIVE);
    }
}
