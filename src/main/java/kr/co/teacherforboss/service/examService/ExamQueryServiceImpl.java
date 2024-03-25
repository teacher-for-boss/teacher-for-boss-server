package kr.co.teacherforboss.service.examService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.ExamHandler;
import kr.co.teacherforboss.converter.ExamConverter;
import kr.co.teacherforboss.domain.ExamCategory;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberExam;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.Tag;
import kr.co.teacherforboss.domain.enums.ExamType;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.ExamCategoryRepository;
import kr.co.teacherforboss.repository.ExamRepository;
import kr.co.teacherforboss.repository.MemberExamRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.repository.TagRepository;
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
    private final QuestionRepository questionRepository;
    private final MemberExamRepository memberExamRepository;
    private final AuthCommandService authCommandService;
    private final TagRepository tagRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ExamCategory> getExamCategories() {
        return examCategoryRepository.findExamCategoriesByStatus(Status.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getTags() {
        return tagRepository.findTagsByStatus(Status.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> getQuestions(Long examId, ExamType examType) {
        if (!examRepository.existsByIdAndStatus(examId, Status.ACTIVE))
            throw new ExamHandler(ErrorStatus.EXAM_NOT_FOUND);

        return questionRepository.findAllByExamIdAndStatus(examId, Status.ACTIVE, Sort.by(Sort.Order.asc("questionSequence")));
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

}
