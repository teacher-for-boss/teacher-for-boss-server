package kr.co.teacherforboss.service.mypageService;

import kr.co.teacherforboss.domain.Question;
import org.springframework.data.domain.Slice;

public interface MypageQueryService {
    Slice<Question> getAnsweredQuestions(Long lastQuestionId, int size);
}
