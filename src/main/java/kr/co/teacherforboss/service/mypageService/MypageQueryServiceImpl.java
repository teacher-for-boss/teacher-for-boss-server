package kr.co.teacherforboss.service.mypageService;

import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.*;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.QuestionBookmarkRepository;
import kr.co.teacherforboss.repository.QuestionLikeRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageQueryServiceImpl implements MypageQueryService{

    private final AuthCommandService authCommandService;
    private final QuestionRepository questionRepository;
    private final QuestionLikeRepository questionLikeRepository;
    private final QuestionBookmarkRepository questionBookmarkRepository;
    private final AnswerRepository answerRepository;

    @Override
    public BoardResponseDTO.GetQuestionsDTO getMyQuestions(Long lastQuestionId, int size) {
        Member member = authCommandService.getMember();
        PageRequest pageRequest = PageRequest.of(0, size);

        Slice<Question> questionsPage;

        if (lastQuestionId == 0) {
            questionsPage = questionRepository.findSliceByStatusAndMemberIdOrderByCreatedAtDesc(Status.ACTIVE, member.getId(), pageRequest);
        } else {
            questionsPage = questionRepository.findSliceByMemberIdAndIdLessThanOrderByCreatedAtDesc(lastQuestionId, member.getId(), pageRequest);
        }

        List<QuestionLike> questionLikes = questionLikeRepository.findByQuestionInAndMemberIdAndStatus(questionsPage.getContent(), member.getId(), Status.ACTIVE);
        List<QuestionBookmark> questionBookmarks = questionBookmarkRepository.findByQuestionInAndMemberIdAndStatus(questionsPage.getContent(), member.getId(), Status.ACTIVE);
        List<Answer> selectedAnswers = answerRepository.findByQuestionInAndSelected(questionsPage.getContent(), BooleanType.T);

        Map<Long, QuestionLike> questionLikeMap = questionLikes.stream()
                .collect(Collectors.toMap(like -> like.getQuestion().getId(), like -> like));
        Map<Long, QuestionBookmark> questionBookmarkMap = questionBookmarks.stream()
                .collect(Collectors.toMap(bookmark -> bookmark.getQuestion().getId(), bookmark -> bookmark));
        Map<Long, Answer> selectedAnswerMap = selectedAnswers.stream()
                .collect(Collectors.toMap(answer -> answer.getQuestion().getId(), answer -> answer));

        return BoardConverter.toGetQuestionsDTO(questionsPage, questionLikeMap, questionBookmarkMap, selectedAnswerMap);
    }
}
