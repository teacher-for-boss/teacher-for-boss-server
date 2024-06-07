package kr.co.teacherforboss.service.boardService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.co.teacherforboss.repository.CategoryRepository;
import kr.co.teacherforboss.repository.HashtagRepository;
import kr.co.teacherforboss.repository.PostHashtagRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.repository.QuestionHashtagRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.service.authService.AuthCommandServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BoardCommandServiceImplTest {

	@InjectMocks @Spy
	private AuthCommandServiceImpl authCommandService;
	@Mock
	private QuestionRepository questionRepository;
	@Mock
	private HashtagRepository hashtagRepository;
	@Mock
	private QuestionHashtagRepository questionHashtagRepository;
	@Mock
	private CategoryRepository categoryRepository;

	@DisplayName("티쳐톡 질문 작성 (성공)")
	@Test
	void saveQuestion() {
		// given


		// when


		// then


		// verify
	}
}
