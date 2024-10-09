package kr.co.teacherforboss.aop;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.AnswerLike;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.Exchange;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Notification;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.ExchangeType;
import kr.co.teacherforboss.domain.enums.NotificationType;
import kr.co.teacherforboss.domain.vo.notificationVO.NotificationLinkData.PostData;
import kr.co.teacherforboss.domain.vo.notificationVO.NotificationLinkData.QuestionData;
import kr.co.teacherforboss.domain.vo.notificationVO.NotificationMessage;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.NotificationRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.service.notificationService.NotificationCommandService;
import kr.co.teacherforboss.service.snsService.SnsService;
import kr.co.teacherforboss.web.dto.HomeResponseDTO.GetHotPostsDTO;
import kr.co.teacherforboss.web.dto.HomeResponseDTO.GetHotPostsDTO.HotPostInfo;
import kr.co.teacherforboss.web.dto.HomeResponseDTO.GetHotQuestionsDTO;
import kr.co.teacherforboss.web.dto.HomeResponseDTO.GetHotQuestionsDTO.HotQuestionInfo;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final SnsService snsService;
    private final NotificationCommandService notificationCommandService;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final PostRepository postRepository;

    /* QUESTION_NEW_ANSWER */
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.boardService.BoardCommandService.saveAnswer(..))", returning = "answer")
    public void sendNewAnswerNotification(Answer answer) {
        log.info("===== Send New Answer Notification =====");

        Member target = answer.getQuestion().getMember();
        Notification notification = notificationRepository.save(
                Notification.builder()
                        .member(target)
                        .type(NotificationType.QUESTION_NEW_ANSWER)
                        .title(NotificationType.QUESTION_NEW_ANSWER.getTitle(answer.getQuestion().getTitle()))
                        .content(NotificationType.QUESTION_NEW_ANSWER.getContent(answer.getMember().getNickname()))
                        .data(new QuestionData(answer.getQuestion().getId()))
                        .build()
        );
        snsService.publishMessage(List.of(notification));
    }

    /* QUESTION_WAITING_ANSWER */
    // 배치 전송
    @Transactional
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

            List<Notification> alreadySentNotifications = notificationRepository.findAllByTypeAndBetweenDate(NotificationType.QUESTION_WAITING_ANSWER, nowDate.minusDays(7), nowDate);
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

            notificationRepository.saveAll(notifications);
            snsService.publishMessage(notifications);
        } while (answers.hasNext());
    }

    /* QUESTION_LAST_DAY_SELECT_ANSWER */
    // 배치 전송
    @Transactional
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

            notificationRepository.saveAll(notifications);
            snsService.publishMessage(notifications);
        } while (questions.hasNext());
    }

    /* QUESTION_ANSWER_SELECTED_AUTO */

    /* QUESTION_AUTO_DELETE_ALERT */
    // 배치 전송
    @Transactional
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

            notificationRepository.saveAll(notifications);
            snsService.publishMessage(notifications);
        } while (questions.hasNext());
    }


    /* QUESTION_AUTO_DELETE */

    /* QUESTION_ANSWER_SELECTED */
    @Transactional
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.boardService.BoardCommandService.selectAnswer(..))", returning = "answer")
    public void sendAnswerSelectedNotification(Answer answer) {
        log.info("===== Send Answer Selected Notification =====");

        Member target = answer.getMember();
        Notification notification = notificationRepository.save(
                Notification.builder()
                        .member(target)
                        .type(NotificationType.QUESTION_ANSWER_SELECTED)
                        .title(NotificationType.QUESTION_ANSWER_SELECTED.getTitle(answer.getQuestion().getTitle()))
                        .content(NotificationType.QUESTION_ANSWER_SELECTED.getContent(answer.getQuestion().getMember().getNickname()))
                        .data(new QuestionData(answer.getQuestion().getId()))
                        .build()
        );

        snsService.publishMessage(List.of(notification));
    }

    /* QUESTION_ANSWER_LIKED */
    @Transactional
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.boardService.BoardCommandService.toggleAnswerLike(..))", returning = "answerLike")
    public void sendAnswerLikedNotification(AnswerLike answerLike) {
        if (!answerLike.getLiked().isIdentifier()) return;

        log.info("===== Send Answer Liked Notification =====");

        Member target = answerLike.getAnswer().getMember();
        Answer answer = answerLike.getAnswer();
        Notification notification = notificationRepository.save(
                Notification.builder()
                        .member(target)
                        .type(NotificationType.QUESTION_ANSWER_LIKED)
                        .title(NotificationType.QUESTION_ANSWER_LIKED.getTitle(answer.getQuestion().getTitle()))
                        .content(NotificationType.QUESTION_ANSWER_LIKED.getContent(answer.getMember().getNickname()))
                        .data(new QuestionData(answer.getQuestion().getId()))
                        .build()
        );

        snsService.publishMessage(List.of(notification));
    }

    /* QUESTION_HOT */
    @Transactional
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.scheduler.HomeScheduler.updateHotQuestions())", returning = "hotQuestionsDTO")
    public void sendHotQuestionNotification(GetHotQuestionsDTO hotQuestionsDTO) {
        log.info("===== Send Hot Question Notification =====");

        List<Question> hotQuestions = questionRepository.findAllById(hotQuestionsDTO.getHotQuestionList().stream().map(HotQuestionInfo::getQuestionId).toList());

        List<Notification> notifications = hotQuestions.stream()
                .map(question -> Notification.builder()
                        .member(question.getMember())
                        .type(NotificationType.QUESTION_HOT)
                        .title(NotificationType.QUESTION_HOT.getTitle(question.getTitle()))
                        .content(NotificationType.QUESTION_HOT.getContent(question.getMember().getNickname()))
                        .data(new QuestionData(question.getId()))
                        .build()
                )
                .toList();

        notificationRepository.saveAll(notifications);
        snsService.publishMessage(notifications);
    }

    /* POST_NEW_COMMENT, POST_COMMENT_NEW_REPLY */
    @Transactional
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.boardService.BoardCommandService.saveComment(..))", returning = "comment")
    public void sendNewCommentNotification(Comment comment) {
        log.info("===== Send New Comment Notification =====");

        Member target;
        Notification notification;

        if (comment.getParent() != null) {
            target = comment.getParent().getMember();
            notification = notificationRepository.save(
                    Notification.builder()
                            .member(target)
                            .type(NotificationType.POST_COMMENT_NEW_REPLY)
                            .title(NotificationType.POST_COMMENT_NEW_REPLY.getTitle(comment.getParent().getContent()))
                            .content(NotificationType.POST_COMMENT_NEW_REPLY.getContent(comment.getMember().getNickname()))
                            .data(new PostData(comment.getPost().getId()))
                            .build()
            );
        }
        else {
            target = comment.getPost().getMember();
            notification = notificationRepository.save(
                    Notification.builder()
                            .member(target)
                            .type(NotificationType.POST_NEW_COMMENT)
                            .title(NotificationType.POST_NEW_COMMENT.getTitle(comment.getPost().getTitle()))
                            .content(NotificationType.POST_NEW_COMMENT.getContent(comment.getMember().getNickname()))
                            .data(new PostData(comment.getPost().getId()))
                            .build()
            );
        }

        snsService.publishMessage(List.of(notification));
    }

    /* POST_VIEW_INCREASED */
    // TODO: 채연언니 PR 합치고 pointcut 다시 보기
    @Transactional
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.domain.Post.increaseViewCount(..))", returning = "post")
    public void sendViewIncreasedNotification(Post post) {
        if (post.getViewCount() % 50 == 0) {
            log.info("===== Send View Increased Notification =====");

            Member target = post.getMember();
            Notification notification = notificationRepository.save(
                    Notification.builder()
                            .member(target)
                            .type(NotificationType.POST_VIEW_INCREASED)
                            .title(NotificationType.POST_VIEW_INCREASED.getTitle(post.getTitle()))
                            .content(NotificationType.POST_VIEW_INCREASED.getContent(post.getViewCount().toString()))
                            .data(new PostData(post.getId()))
                            .build()
            );

            snsService.publishMessage(List.of(notification));
        }
    }

    /* POST_HOT */
    @Transactional
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.scheduler.HomeScheduler.updateHotPosts())", returning = "hotPostsDTO")
    public void sendHotPostNotification(GetHotPostsDTO hotPostsDTO) {
        log.info("===== Send Hot Post Notification =====");

        List<Post> hotPosts = postRepository.findAllById(hotPostsDTO.getHotPostList().stream().map(HotPostInfo::getPostId).toList());

        List<Notification> notifications = hotPosts.stream()
                .map(post -> Notification.builder()
                        .member(post.getMember())
                        .type(NotificationType.POST_HOT)
                        .title(NotificationType.POST_HOT.getTitle(post.getTitle()))
                        .content(NotificationType.POST_HOT.getContent(post.getMember().getNickname()))
                        .data(new PostData(post.getId()))
                        .build()
                )
                .toList();

        notificationRepository.saveAll(notifications);
        snsService.publishMessage(notifications);
    }

    /* HOME_NEW_HOT_TEACHERS */
    // 전체 알림
    @Async
    @After(value = "execution(* kr.co.teacherforboss.scheduler.HomeScheduler.updateHotTeachers())")
    public void sendNewHotTeachersNotification() {
        log.info("===== Send New Hot Teachers Notification =====");

        int page = 0;
        int batchSize = 100;

        Page<Member> members;
        List<Notification> notifications;
        LocalDateTime now = LocalDateTime.now();

        do {
            members = memberRepository.findAll(PageRequest.of(page++, batchSize));

            notifications = members.stream()
                    .map(member -> Notification.builder()
                            .member(member)
                            .type(NotificationType.HOME_NEW_HOT_TEACHERS)
                            .title(NotificationType.HOME_NEW_HOT_TEACHERS.getTitle())
                            .content(NotificationType.HOME_NEW_HOT_TEACHERS.getContent())
                            .data(null)
                            .isRead(BooleanType.F)
                            .sentAt(now)    // TODO: 여기 이렇게 하는게 맞을지 나중에 다시 보기
                            .build()
                    )
                    .toList();

            notificationRepository.saveAll(notifications);
        } while (members.hasNext());

        snsService.publishMessage(NotificationMessage.from(notifications.get(0)).getMessage());
    }

    /* EXCHANGE_COMPLETE */
    @Transactional
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.paymentService.PaymentCommandService.completeExchangeProcess())", returning = "exchange")
    public void sendExchangeCompleteNotification(Exchange exchange) {
        if (exchange.getExchangeType() != ExchangeType.EX) return;

        log.info("===== Send Exchange Complete Notification =====");

        Member target = exchange.getMember();
        Notification notification = notificationRepository.save(
                Notification.builder()
                        .member(target)
                        .type(NotificationType.EXCHANGE_COMPLETE)
                        .title(NotificationType.EXCHANGE_COMPLETE.getTitle())
                        .content(NotificationType.EXCHANGE_COMPLETE.getContent(exchange.getPoints().toString()))
                        .data(null)
                        .build()
        );

        snsService.publishMessage(List.of(notification));
    }

    /* 알림 읽음 처리 */
    @AfterReturning(value = "execution(* kr.co.teacherforboss.service.notificationService.NotificationQueryService.getNotifications(..))", returning = "notificationsDTO")
    public void readNotification(NotificationResponseDTO.GetNotificationsDTO notificationsDTO) {
        notificationCommandService.readNotifications(notificationsDTO.getNotificationList()
                .stream().map(NotificationResponseDTO.GetNotificationsDTO.NotificationInfo::getNotificationId).toList());
    }

}
