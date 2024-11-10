package kr.co.teacherforboss.scheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Notification;
import kr.co.teacherforboss.domain.NotificationSetting;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.NotificationType;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.domain.vo.notificationVO.NotificationLinkData.QuestionData;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.NotificationRepository;
import kr.co.teacherforboss.repository.NotificationSettingRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.service.snsService.SnsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("prod")
public class NotificationScheduler {

    private final SnsService snsService;
    private final QuestionRepository questionRepository;
    private final NotificationRepository notificationRepository;
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    /* QUESTION_WAITING_ANSWER */
    // 배치 전송
    @Scheduled(cron = "0 0 9 * * ?")    // 매일 오전 9시
    public void sendWaitingAnswerNotification() {
        log.info("===== Send Waiting Answer Notification =====");

        int page = 0;
        int batchSize = 100;

        Slice<Answer> answers;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowDate = LocalDate.now().atStartOfDay();

        do {
            answers = answerRepository.findFirstAnswersOfNotSolvedQuestion(nowDate.minusDays(7), nowDate, PageRequest.of(page++, batchSize));

            List<Notification> alreadySentNotifications = notificationRepository.findAllByTypeAndBetweenDate(
                    NotificationType.QUESTION_WAITING_ANSWER.name(), nowDate.minusDays(7), nowDate);
            Map<Long, Integer> notificationSentDayInfo = alreadySentNotifications.stream()
                    .collect(Collectors.toMap(
                            notification -> {
                                QuestionData questionData = (QuestionData) notification.getData();
                                return questionData.getQuestionId();
                            },
                            notification -> (int) Duration.between(notification.getSentAt(), nowDate).toDays()
                    ));

            List<Question> questionsToSend = answers.stream()
                    .filter(answer -> {
                        if (!notificationSentDayInfo.containsKey(answer.getQuestion().getId())) {
                            return answer.getQuestion().getClosedDate().isBefore(nowDate.toLocalDate()) &&
                                    Duration.between(answer.getCreatedAt(), now).toDays() >= 3;
                        }
                        return notificationSentDayInfo.get(answer.getQuestion().getId()) >= 3;
                    })
                    .map(Answer::getQuestion)
                    .collect(Collectors.toCollection(ArrayList::new));

            List<Notification> notifications = questionsToSend.stream()
                    .map(question -> Notification.builder()
                            .member(question.getMember())
                            .type(NotificationType.QUESTION_WAITING_ANSWER)
                            .title(NotificationType.QUESTION_WAITING_ANSWER.getTitle(question.getTitle()))
                            .content(NotificationType.QUESTION_WAITING_ANSWER.getContent())
                            .data(new QuestionData(question.getId()))
                            .sentAt(now)
                            .build()
                    )
                    .toList();

            notifications = notifications.stream().filter(notification -> agreeNotification(notification.getMember(), notification.getType())).toList();
            notificationRepository.saveAll(notifications);
            snsService.publishMessage(notifications);
        } while (answers.hasNext());
    }

    /* QUESTION_LAST_DAY_SELECT_ANSWER */
    // 배치 전송
    @Scheduled(cron = "0 0 9 * * ?")    // 매일 오전 9시
    public void sendLastDaySelectAnswerNotification() {
        log.info("===== Send Last Day Select Answer Notification =====");

        int page = 0;
        int batchSize = 100;

        Slice<Question> questions;
        List<Notification> notifications;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowDate = LocalDate.now().atStartOfDay();

        do {
            questions = questionRepository.findQuestionsLastDaySelectByDate(nowDate.minusDays(7), PageRequest.of(page++, batchSize));

            notifications = questions.stream()
                    .map(question -> Notification.builder()
                            .member(question.getMember())
                            .type(NotificationType.QUESTION_LAST_DAY_SELECT_ANSWER)
                            .title(NotificationType.QUESTION_LAST_DAY_SELECT_ANSWER.getTitle(question.getTitle()))
                            .content(NotificationType.QUESTION_LAST_DAY_SELECT_ANSWER.getContent())
                            .data(new QuestionData(question.getId()))
                            .sentAt(now)
                            .build()
                    )
                    .toList();

            notifications = notifications.stream().filter(notification -> agreeNotification(notification.getMember(), notification.getType())).toList();
            notificationRepository.saveAll(notifications);
            snsService.publishMessage(notifications);
        } while (questions.hasNext());
    }

    /* QUESTION_AUTO_DELETE_ALERT */
    // 배치 전송
    @Scheduled(cron = "0 0 9 * * ?")    // 매일 오전 9시
    public void sendAutoDeleteAlertNotification() {
        log.info("===== Send Auto Delete Alert Notification =====");

        int page = 0;
        int batchSize = 100;

        Slice<Question> questions;
        List<Notification> notifications;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowDate = LocalDate.now().atStartOfDay();

        do {
            questions = questionRepository.findQuestionsForAutoDeleteAlertByDate(nowDate.minusDays(6), PageRequest.of(page++, batchSize));

            notifications = questions.stream()
                    .map(question -> Notification.builder()
                            .member(question.getMember())
                            .type(NotificationType.QUESTION_AUTO_DELETE_ALERT)
                            .title(NotificationType.QUESTION_AUTO_DELETE_ALERT.getTitle(question.getTitle()))
                            .content(NotificationType.QUESTION_AUTO_DELETE_ALERT.getContent())
                            .data(new QuestionData(question.getId()))
                            .sentAt(now)
                            .build()
                    )
                    .toList();

            notifications = notifications.stream().filter(notification -> agreeNotification(notification.getMember(), notification.getType())).toList();
            notificationRepository.saveAll(notifications);
            snsService.publishMessage(notifications);
        } while (questions.hasNext());
    }

    /* QUESTION_NEW */
    @Scheduled(cron = "0 0 15,20 * * ?")
    public void sendNewQuestionNotification() {
        log.info("===== Send New Question Notification =====");

        int page = 0;
        int batchSize = 100;

        Page<Member> members;
        List<Notification> notifications;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reqDateTime;
        if (now.getHour() <= 15) reqDateTime = LocalDate.now().minusDays(1).atTime(20, 0);
        else reqDateTime = LocalDate.now().atTime(15, 0);

        long newQuestionCount = questionRepository.countByCreatedAtGreaterThanEqualAndStatus(reqDateTime, Status.ACTIVE);
        if (newQuestionCount == 0) return;

        do {
            members = memberRepository.findAllAgreeServiceNotificationByRole(
                    Role.TEACHER.name(), PageRequest.of(page++, batchSize));

            notifications = members.stream()
                    .map(member -> Notification.builder()
                            .member(member)
                            .type(NotificationType.QUESTION_NEW)
                            .title(NotificationType.QUESTION_NEW.getTitle())
                            .content(NotificationType.QUESTION_NEW.getContent(String.valueOf(newQuestionCount)))
                            .data(null)
                            .isRead(BooleanType.F)
                            .sentAt(now)    // TODO: 여기 이렇게 하는게 맞을지 나중에 다시 보기
                            .build()
                    )
                    .toList();

            notificationRepository.saveAll(notifications);
        } while (members.hasNext());

        snsService.publishMessage(notifications.get(0));
    }

    private boolean agreeNotification(Member target, NotificationType type) {
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(target.getId()).orElse(null);
        if (notificationSetting == null || notificationSetting.getService().equals(BooleanType.F)) return false;
        return true;
    }
}
