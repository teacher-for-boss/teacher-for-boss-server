package kr.co.teacherforboss.scheduler;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.BoardHandler;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.TeacherSelectInfo;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.repository.TeacherSelectInfoRepository;
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

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkExpiredQuestions() {
    System.out.println("현재 시각은 " + new Date());
        List<Question> expiredQuestions = questionRepository.findByExpiredDate();

        for (Question question : expiredQuestions) {
            if (question.getAnswerList().isEmpty()) { // 답변 자동 삭제
                questionRepository.delete(question);
                // TODO: 질문권 복구 및 질문글 삭제에 대한 푸시알림 추가
            } else { // 답변 채택
                // TODO: 채택된 답변을 단 티쳐 status INACTIVE면 ? -> 연관관계 처리
                Answer bestAnswer = answerRepository.findTopByQuestionIdAndSelectedAtIsNullAndStatusOrderByLikeCountDescDislikeCountAscCreatedAtAsc(question.getId(), Status.ACTIVE)
                        .orElseThrow(() -> new BoardHandler(ErrorStatus.ANSWER_NOT_FOUND));
                System.out.println(bestAnswer.getId());
                System.out.println(bestAnswer.getQuestion().getId());

                question.selectAnswer(bestAnswer);
                TeacherSelectInfo teacherSelectInfo = teacherSelectInfoRepository.findByMemberIdAndStatus(bestAnswer.getMember().getId(), Status.ACTIVE)
                            .orElseThrow(() -> new BoardHandler(ErrorStatus.TEACHER_SELECT_INFO_NOT_FOUND));
                teacherSelectInfo.increaseSelectCount();
            }
        }
    }
}
