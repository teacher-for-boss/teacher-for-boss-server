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
import kr.co.teacherforboss.domain.MemberChoice;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Problem;
import kr.co.teacherforboss.domain.ProblemChoice;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.ExamRepository;
import kr.co.teacherforboss.repository.MemberChoiceRepository;
import kr.co.teacherforboss.repository.MemberExamRepository;
import kr.co.teacherforboss.repository.ProblemChoiceRepository;
import kr.co.teacherforboss.repository.ProblemRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.ExamRequestDTO;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExamCommandServiceImpl implements ExamCommandService {

    private final ProblemRepository problemRepository;
    private final ProblemChoiceRepository problemChoiceRepository;
    private final ExamRepository examRepository;
    private final MemberExamRepository memberExamRepository;
    private final MemberChoiceRepository memberChoiceRepository;
    private final AuthCommandService authCommandService;

    @Override
    @Transactional
    public MemberExam takeExam(Long examId, ExamRequestDTO.TakeExamDTO request) {
        Member member = authCommandService.getMember();

        Exam exam = examRepository.findByIdAndStatus(examId, Status.ACTIVE)
                .orElseThrow(() -> new ExamHandler(ErrorStatus.EXAM_NOT_FOUND));

        if (problemRepository.countByExamIdAndStatus(examId, Status.ACTIVE) != request.getProblemAnsList().size())
            throw new ExamHandler(ErrorStatus.INVALID_EXAM_TAKE);

        List<Long> problemIds = request.getProblemAnsList().stream()
                .map(ExamRequestDTO.TakeExamChoiceDTO::getProblemId)
                .toList();
        List<Problem> problems = problemRepository.findByIdInAndStatus(problemIds, Status.ACTIVE);
        if (problems.size() != problemIds.size())
            throw new ExamHandler(ErrorStatus.PROBLEM_NOT_FOUND);
        Map<Long, Problem> problemMap = problems.stream()
                .collect(Collectors.toMap(Problem::getId, Function.identity()));

        List<Long> problemChoiceIds = request.getProblemAnsList().stream()
                .map(ExamRequestDTO.TakeExamChoiceDTO::getProblemChoiceId)
                .toList();
        List<ProblemChoice> choices = problemChoiceRepository.findByIdInAndStatus(problemChoiceIds, Status.ACTIVE);
        if (choices.size() != problemChoiceIds.size())
            throw new ExamHandler(ErrorStatus.PROBLEM_CHOICE_NOT_FOUND);
        Map<Long, ProblemChoice> problemChoiceMap = choices.stream()
                .collect(Collectors.toMap(ProblemChoice::getId, Function.identity()));

        AtomicInteger score = new AtomicInteger(0);
        request.getProblemAnsList().forEach(q -> {
            Problem problem = problemMap.get(q.getProblemId());
            ProblemChoice problemChoice = problemChoiceMap.get(q.getProblemChoiceId());
            if (!problemChoice.getProblem().getId().equals(problem.getId()))
                throw new ExamHandler(ErrorStatus.INVALID_PROBLEM_CHOICE);
            if (problemChoice.isCorrect()) {
                score.addAndGet(problem.getPoints());
            }
        });

        MemberExam memberExam = ExamConverter.toMemberExam(member, exam, score.intValue(), request.getLeftTime());

        List<MemberChoice> memberChoiceList = request.getProblemAnsList().stream().map(p -> {
            Problem problem = problemMap.get(p.getProblemId());
            ProblemChoice problemChoice = problemChoiceMap.get(p.getProblemChoiceId());
            return ExamConverter.toMemberChoice(memberExam, problem, problemChoice);
        }).toList();
        memberChoiceRepository.saveAll(memberChoiceList);

        return memberExamRepository.save(memberExam);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponseDTO.GetExamResultDTO getExamResult(Long memberExamId) {
        Member member = authCommandService.getMember();

        MemberExam memberExam = memberExamRepository.findByIdAndMemberAndStatus(memberExamId, member, Status.ACTIVE)
                .orElseThrow(() -> new ExamHandler(ErrorStatus.MEMBER_EXAM_NOT_FOUND));

        int problemsCount =  memberExam.getExam().getProblemList().size();
        int score = memberExam.getScore();

        List<MemberChoice> memberChoices = memberChoiceRepository.findAllByMemberExamIdAndStatus(memberExam.getId(), Status.ACTIVE);
        int correctChoicesCount = memberChoices.stream()
                .filter(q -> q.getProblemChoice().isCorrect()).mapToInt(e -> 1).sum();
        int incorrectChoicesCount = memberChoices.size() - correctChoicesCount;

        return ExamConverter.toGetExamResultDTO(memberExam.getId(), score, problemsCount, correctChoicesCount, incorrectChoicesCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Problem> getExamIncorrectChoices(Long memberExamId) {
        Member member = authCommandService.getMember();
        MemberExam memberExam = memberExamRepository.findByIdAndMemberAndStatus(memberExamId, member, Status.ACTIVE)
                .orElseThrow(() -> new ExamHandler(ErrorStatus.MEMBER_EXAM_NOT_FOUND));

        List<MemberChoice> memberIncorrectChoices = memberChoiceRepository.findIncorrectChoices(memberExam, Status.ACTIVE);
        return memberIncorrectChoices.stream().map(MemberChoice::getProblem).toList();
    }
}
