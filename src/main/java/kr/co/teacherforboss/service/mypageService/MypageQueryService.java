package kr.co.teacherforboss.service.mypageService;

import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import org.springframework.data.domain.Slice;

public interface MypageQueryService {
    Slice<Question> getAnsweredQuestions(Long lastQuestionId, int size);
    BoardResponseDTO.GetPostsDTO getAnsweredPosts(Long lastPostId, int size);
}
