package kr.co.teacherforboss.service.examService;

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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    public MemberExam takeExams(Long examId, ExamRequestDTO.TakeExamsDTO request) {
        Member member = authCommandService.getMember();
        AtomicInteger score = new AtomicInteger(0);

        Exam exam = examRepository.findByIdAndStatus(examId, Status.ACTIVE)
                .orElseThrow(() -> new ExamHandler(ErrorStatus.EXAM_NOT_FOUND));

        if (memberExamRepository.existsByMemberIdAndExamId(member.getId(), examId))
            throw new ExamHandler(ErrorStatus.MEMBER_EXAM_DUPLICATE);

        if (questionRepository.countByExamIdAndStatus(examId, Status.ACTIVE) != request.getQuestionAnsList().size())
            throw new ExamHandler(ErrorStatus.INVALID_QUESTION_CHOICE);

        MemberExam memberExam = ExamConverter.toMemberExam(member, exam);

        List<MemberAnswer> memberAnswerList = request.getQuestionAnsList().stream()
                .map(q -> {
                    Question question = questionRepository.findByIdAndStatus(q.getQuestionId(), Status.ACTIVE)
                            .orElseThrow(() -> new ExamHandler(ErrorStatus.QUESTION_NOT_FOUND));
                    QuestionChoice questionChoice = questionChoiceRepository.findByIdAndStatus(q.getQuestionChoiceId(), Status.ACTIVE)
                            .orElseThrow(() -> new ExamHandler(ErrorStatus.QUESTION_CHOICE_NOT_FOUND));

                    if (question.getAnswer().equals(questionChoice.getChoice()))
                        score.addAndGet(question.getPoints());

                    return ExamConverter.toMemberAnswer(question, questionChoice);
                }).collect(Collectors.toList());

        memberExam.setScore(score.intValue());

        memberAnswerList.forEach(memberAnswer -> {memberAnswer.setMemberExam(memberExam);});
        memberAnswerRepository.saveAll(memberAnswerList);

        return memberExamRepository.save(memberExam);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponseDTO.GetExamResultDTO getExamResult(Long examId) {
        Member member = authCommandService.getMember();

        if (!examRepository.existsByIdAndStatus(examId, Status.ACTIVE))
            throw new ExamHandler(ErrorStatus.EXAM_NOT_FOUND);

        MemberExam memberExam = memberExamRepository.findByMemberIdAndExamIdAndStatus(member.getId(), examId, Status.ACTIVE)
                .orElseThrow(() -> new ExamHandler(ErrorStatus.MEMBER_EXAM_NOT_FOUND));

        int questionsNum = questionRepository.countByExamIdAndStatus(examId, Status.ACTIVE);
        int score = memberExam.getScore();
        AtomicInteger correctAnsNum = new AtomicInteger(0);
        AtomicInteger incorrectAnsNum = new AtomicInteger(0);

        memberAnswerRepository.findAllByMemberExamIdAndStatus(memberExam.getId(), Status.ACTIVE)
                .forEach(q -> {
                    if (q.getQuestion().getAnswer().equals(q.getQuestionChoice().getChoice())) {
                        correctAnsNum.incrementAndGet();
                    } else {
                        incorrectAnsNum.incrementAndGet();
                    }
                });

        return ExamConverter.toGetExamResultDTO(score, questionsNum, correctAnsNum, incorrectAnsNum);
    }
}
