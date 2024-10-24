package kr.co.teacherforboss.service.examService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.ExamHandler;
import kr.co.teacherforboss.converter.ExamConverter;
import kr.co.teacherforboss.domain.Exam;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberChoice;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Problem;
import kr.co.teacherforboss.domain.ExamTag;
import kr.co.teacherforboss.domain.enums.ExamQuarter;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.ExamCategoryRepository;
import kr.co.teacherforboss.repository.ExamRepository;
import kr.co.teacherforboss.repository.MemberChoiceRepository;
import kr.co.teacherforboss.repository.MemberExamRepository;
import kr.co.teacherforboss.repository.ProblemRepository;
import kr.co.teacherforboss.repository.ExamTagRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.ExamResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExamQueryServiceImpl implements ExamQueryService {

    private final ExamRepository examRepository;
    private final ExamCategoryRepository examCategoryRepository;
    private final ProblemRepository problemRepository;
    private final MemberExamRepository memberExamRepository;
    private final AuthCommandService authCommandService;
    private final ExamTagRepository examTagRepository;
    private final MemberChoiceRepository memberChoiceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ExamCategory> getExamCategories() {
        return examCategoryRepository.findExamCategoriesByStatus(Status.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamTag> getExamTags(Long examCategoryId) {
        if (!examCategoryRepository.existsByIdAndStatus(examCategoryId, Status.ACTIVE))
            throw new ExamHandler(ErrorStatus.EXAM_CATEGORY_NOT_FOUND);

        return examTagRepository.findExamTagsByExamCategoryIdAndStatus(examCategoryId, Status.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getExams(Long examCategoryId, Long examTagId) {
        if(!examCategoryRepository.existsByIdAndStatus(examCategoryId, Status.ACTIVE))
            throw new ExamHandler(ErrorStatus.EXAM_CATEGORY_NOT_FOUND);

        List<Exam> examList;

        if (examTagId == null) {
            examList = examRepository.findByExamCategoryIdAndStatus(examCategoryId, Status.ACTIVE);
        } else {
            if(!examTagRepository.existsByIdAndExamCategoryIdAndStatus(examTagId, examCategoryId, Status.ACTIVE))
                throw new ExamHandler(ErrorStatus.EXAM_TAG_NOT_FOUND);
            examList = examRepository.findByExamCategoryIdAndExamTagIdAndStatus(examCategoryId, examTagId, Status.ACTIVE);
        }
        return examList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberExam> getMemberExams() {
        Member member = authCommandService.getMember();
        return memberExamRepository.findAllRecentByMemberId(member.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Problem> getProblems(Long examId) {
        if (!examRepository.existsByIdAndStatus(examId, Status.ACTIVE))
            throw new ExamHandler(ErrorStatus.EXAM_NOT_FOUND);

        return problemRepository.findAllByExamIdAndStatus(examId, Status.ACTIVE, Sort.by(Sort.Order.asc("sequence")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDTO.GetExamRankInfoDTO.ExamRankInfo> getExamRankInfo(Long examId) {
        Member member = authCommandService.getMember();
        List<ExamResponseDTO.GetExamRankInfoDTO.ExamRankInfo> examRankInfos = new ArrayList<>();

        if (!examRepository.existsByIdAndStatus(examId, Status.ACTIVE))
            throw new ExamHandler(ErrorStatus.EXAM_NOT_FOUND);

        List<MemberExam> top3 = memberExamRepository.findTop3ByExamId(examId);

        boolean inTop3 = false;
        for (int i = 0; i < top3.size(); i++) {
            if (Objects.equals(top3.get(i).getMember().getId(), member.getId())) {
                examRankInfos.add(ExamConverter.toGetExamRankInfo(top3.get(i), (long) i + 1, true));
                inTop3 = true;
            } else {
                examRankInfos.add(ExamConverter.toGetExamRankInfo(top3.get(i), (long) i + 1, false));
            }
        }

        if (!inTop3) {
            MemberExam mine = memberExamRepository.findRecentByMemberIdAndExamId(member.getId(), examId)
                    .orElseThrow(() -> new ExamHandler(ErrorStatus.MEMBER_EXAM_NOT_FOUND));
            Long rank = memberExamRepository.findRankById(mine.getId());
            examRankInfos.add(ExamConverter.toGetExamRankInfo(mine, rank, true));
        } else {
            MemberExam last = memberExamRepository.findBottom1ByExamId(examId);
            Long rank = memberExamRepository.findRankById(last.getId());
            boolean isLastInTop3 = top3.stream().anyMatch(exam -> Objects.equals(exam.getId(), last.getId()));
            if (!isLastInTop3) {
                examRankInfos.add(ExamConverter.toGetExamRankInfo(last, rank, false));
            }
        }

        return examRankInfos;
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponseDTO.GetAverageDTO getAverage(ExamQuarter examQuarter){
        Member member = authCommandService.getMember();

        Integer userScore = memberExamRepository.getAverageByMemberId(member.getId(), examQuarter.getFirst(), examQuarter.getLast())
                .orElseThrow(() -> new ExamHandler(ErrorStatus.MEMBER_EXAM_HISTORY_NOT_FOUND));
        Integer averageScore = memberExamRepository.getAverageByMemberIdNot(member.getId(), examQuarter.getFirst(), examQuarter.getLast())
                .orElseThrow(() -> new ExamHandler(ErrorStatus.EXAM_AVERAGE_NOT_FOUND));

        return ExamConverter.toGetAverageDTO(averageScore, userScore);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getTakenExams() {
        Member member = authCommandService.getMember();
        List<Exam> exams = examRepository.findAllTakenExamByMemberId(member.getId());
        return exams;
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

        List<MemberChoice> memberIncorrectChoices = memberChoiceRepository.findIncorrectMemberChoices(memberExam, Status.ACTIVE);
        return memberIncorrectChoices.stream().map(MemberChoice::getProblem).toList();
    }
}
