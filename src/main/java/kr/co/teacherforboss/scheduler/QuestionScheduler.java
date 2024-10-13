package kr.co.teacherforboss.scheduler;

import java.time.LocalDate;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.TeacherSelectInfo;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.repository.TeacherSelectInfoRepository;
import kr.co.teacherforboss.service.boardService.BoardCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionScheduler {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final TeacherSelectInfoRepository teacherSelectInfoRepository;
    private final BoardCommandService boardCommandService;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkExpiredQuestions() {
        log.info("===== handle expired questions =====");
        List<Question> expiredQuestions = questionRepository.findByExpiredDate(LocalDate.now());

        for (Question question : expiredQuestions) {
            if (question.getAnswerList().isEmpty()) { // 답변 자동 삭제 -> 해야할까 ? 티처 - 마이페이지에서 내가 답변한 글 조회 -> 눌렀을 때 삭제된 글입니다. 가 더 낫지 않나
                System.out.println("Auto Delete Question : " + question.getId());
                autoDeleteQuestion(question);
            } else { // 답변 채택
                // TODO: 채택된 답변을 단 티쳐 status INACTIVE면 ? -> 연관관계 처리
                System.out.println("Auto Solved Question : " + question.getId());
                autoSelectAnswer(question);
            }
        }
    }

    public void autoDeleteQuestion(Question question) {
        question.softDelete();
    }

    public void autoSelectAnswer(Question question) {
        Answer bestAnswer = answerRepository.findTopByQuestionIdAndSelectedAtIsNullAndStatusOrderByLikeCountDescDislikeCountAscCreatedAtAsc(question.getId(), Status.ACTIVE)
                .orElseThrow(() -> new BoardHandler(ErrorStatus.ANSWER_NOT_FOUND));
        boardCommandService.selectAnswer(question.getId(), bestAnswer.getId());
    }
}
