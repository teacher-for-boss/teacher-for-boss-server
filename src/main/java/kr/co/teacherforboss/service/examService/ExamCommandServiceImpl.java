package kr.co.teacherforboss.service.examService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.ExamHandler;
import kr.co.teacherforboss.converter.ExamConverter;
import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberAnswer;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.QuestionChoice;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.ExamRepository;
import kr.co.teacherforboss.repository.MemberAnswerRepository;
import kr.co.teacherforboss.repository.MemberExamRepository;
import kr.co.teacherforboss.repository.QuestionChoiceRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.ExamRequestDTO;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExamCommandServiceImpl implements ExamCommandService {

    private final QuestionRepository questionRepository;
    private final QuestionChoiceRepository questionChoiceRepository;
    private final ExamRepository examRepository;
    private final MemberExamRepository memberExamRepository;
    private final MemberAnswerRepository memberAnswerRepository;
    private final AuthCommandService authCommandService;

    @Override
    @Transactional
    public MemberExam takeExam(Long examId, ExamRequestDTO.TakeExamDTO request) {
        Member member = authCommandService.getMember();

        Exam exam = examRepository.findByIdAndStatus(examId, Status.ACTIVE)
                .orElseThrow(() -> new ExamHandler(ErrorStatus.EXAM_NOT_FOUND));

        if (questionRepository.countByExamIdAndStatus(examId, Status.ACTIVE) != request.getQuestionAnsList().size())
            throw new ExamHandler(ErrorStatus.INVALID_EXAM_TAKE);

        List<Long> questionIds = request.getQuestionAnsList().stream()
                .map(ExamRequestDTO.TakeExamChoiceDTO::getQuestionId)
                .toList();
        List<Question> questions = questionRepository.findByIdInAndStatus(questionIds, Status.ACTIVE);
        if (questions.size() != questionIds.size())
            throw new ExamHandler(ErrorStatus.QUESTION_NOT_FOUND);
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        List<Long> questionChoiceIds = request.getQuestionAnsList().stream()
                .map(ExamRequestDTO.TakeExamChoiceDTO::getQuestionChoiceId)
                .toList();
        List<QuestionChoice> choices = questionChoiceRepository.findByIdInAndStatus(questionChoiceIds, Status.ACTIVE);
        if (choices.size() != questionChoiceIds.size())
            throw new ExamHandler(ErrorStatus.QUESTION_CHOICE_NOT_FOUND);
        Map<Long, QuestionChoice> questionChoiceMap = choices.stream()
                .collect(Collectors.toMap(QuestionChoice::getId, Function.identity()));

        AtomicInteger score = new AtomicInteger(0);
        request.getQuestionAnsList().forEach(q -> {
            Question question = questionMap.get(q.getQuestionId());
            QuestionChoice questionChoice = questionChoiceMap.get(q.getQuestionChoiceId());
            if (!questionChoice.getQuestion().getId().equals(question.getId()))
                throw new ExamHandler(ErrorStatus.INVALID_QUESTION_CHOICE);
            if (questionChoice.isCorrect()) {
                score.addAndGet(question.getPoints());
            }
        });

        MemberExam memberExam = ExamConverter.toMemberExam(member, exam, score.intValue(), request.getLeftTime());

        List<MemberAnswer> memberAnswerList = request.getQuestionAnsList().stream().map(q -> {
            Question question = questionMap.get(q.getQuestionId());
            QuestionChoice questionChoice = questionChoiceMap.get(q.getQuestionChoiceId());
            return ExamConverter.toMemberAnswer(memberExam, question, questionChoice);
        }).toList();
        memberAnswerRepository.saveAll(memberAnswerList);

        return memberExamRepository.save(memberExam);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponseDTO.GetExamResultDTO getExamResult(Long examId) {
        Member member = authCommandService.getMember();

        if (!examRepository.existsByIdAndStatus(examId, Status.ACTIVE))
            throw new ExamHandler(ErrorStatus.EXAM_NOT_FOUND);

        MemberExam memberExam = memberExamRepository.findFirstByMemberIdAndExamIdAndStatusOrderByCreatedAtDesc(member.getId(), examId, Status.ACTIVE)
                .orElseThrow(() -> new ExamHandler(ErrorStatus.MEMBER_EXAM_NOT_FOUND));

        int questionsNum = questionRepository.countByExamIdAndStatus(examId, Status.ACTIVE);
        int score = memberExam.getScore();

        List<MemberAnswer> memberAnswers = memberAnswerRepository.findAllByMemberExamIdAndStatus(memberExam.getId(), Status.ACTIVE);
        int correctAnsNum = memberAnswers.stream()
                .filter(q -> q.getQuestion().getAnswer().equals(q.getQuestionChoice().getChoice())).mapToInt(e -> 1).sum();
        int incorrectAnsNum = memberAnswers.size() - correctAnsNum;

        return ExamConverter.toGetExamResultDTO(memberExam.getId(), score, questionsNum, correctAnsNum, incorrectAnsNum);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> getExamIncorrectAnswers(Long memberExamId) {
        Member member = authCommandService.getMember();
        MemberExam memberExam = memberExamRepository.findByIdAndMemberAndStatus(memberExamId, member, Status.ACTIVE)
                .orElseThrow(() -> new ExamHandler(ErrorStatus.MEMBER_EXAM_NOT_FOUND));

        List<MemberAnswer> memberIncorrectAnswers = memberAnswerRepository.findIncorrectAnswers(memberExam, Status.ACTIVE);
        return memberIncorrectAnswers.stream().map(MemberAnswer::getQuestion).toList();
    }
}
