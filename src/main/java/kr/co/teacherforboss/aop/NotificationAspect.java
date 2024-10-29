package kr.co.teacherforboss.aop;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.co.teacherforboss.config.RedisConfig;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.AnswerLike;
import kr.co.teacherforboss.domain.Comment;
import kr.co.teacherforboss.domain.Exchange;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Notification;
import kr.co.teacherforboss.domain.NotificationSetting;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.ExchangeType;
import kr.co.teacherforboss.domain.enums.NotificationType;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.domain.vo.notificationVO.NotificationLinkData.PostData;
import kr.co.teacherforboss.domain.vo.notificationVO.NotificationLinkData.QuestionData;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.NotificationRepository;
import kr.co.teacherforboss.repository.NotificationSettingRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.service.notificationService.NotificationCommandService;
import kr.co.teacherforboss.service.snsService.SnsService;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import kr.co.teacherforboss.web.dto.HomeResponseDTO.GetHotPostsDTO;
import kr.co.teacherforboss.web.dto.HomeResponseDTO.GetHotPostsDTO.HotPostInfo;
import kr.co.teacherforboss.web.dto.HomeResponseDTO.GetHotQuestionsDTO;
import kr.co.teacherforboss.web.dto.HomeResponseDTO.GetHotQuestionsDTO.HotQuestionInfo;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NotificationAspect {

    private final SnsService snsService;
    private final NotificationCommandService notificationCommandService;
    private final NotificationSettingRepository notificationSettingRepository;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final PostRepository postRepository;
    private final CacheManager cacheManager;

    /* QUESTION_NEW_ANSWER */
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.boardService.BoardCommandService.saveAnswer(..))", returning = "answer")
    public void sendNewAnswerNotification(Answer answer) {
        log.info("===== Send New Answer Notification =====");

        Member target = answer.getQuestion().getMember();
        if (target.getId().equals(answer.getMember().getId())) return;
        if (!agreeNotification(target, NotificationType.QUESTION_NEW_ANSWER)) return;

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

            List<Notification> alreadySentNotifications = notificationRepository.findAllByTypeAndBetweenDate(NotificationType.QUESTION_WAITING_ANSWER.name(), nowDate.minusDays(7), nowDate);
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


    /* QUESTION_AUTO_DELETE */
    @After(value = "execution(* kr.co.teacherforboss.scheduler.QuestionScheduler.autoDeleteQuestion(..)) && args(question)")
    public void sendAutoDeleteQuestionNotification(Question question) {
        log.info("===== Send Auto Delete Question Notification =====");

        Member target = question.getMember();
        if (!agreeNotification(target, NotificationType.QUESTION_AUTO_DELETE)) return;

        Notification notification = notificationRepository.save(
                Notification.builder()
                        .member(target)
                        .type(NotificationType.QUESTION_AUTO_DELETE)
                        .title(NotificationType.QUESTION_AUTO_DELETE.getTitle(question.getTitle()))
                        .content(NotificationType.QUESTION_AUTO_DELETE.getContent())
                        .build()
        );

        snsService.publishMessage(List.of(notification));
    }

    /* QUESTION_ANSWER_SELECTED */
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.boardService.BoardCommandService.selectAnswer(..))", returning = "answer")
    public void sendAnswerSelectedNotification(Answer answer) {
        log.info("===== Send Answer Selected Notification =====");

        Member target = answer.getMember();
        if (!agreeNotification(target, NotificationType.QUESTION_ANSWER_SELECTED)) return;

        Notification notification = notificationRepository.save(
                Notification.builder()
                        .member(target)
                        .type(NotificationType.QUESTION_ANSWER_SELECTED)
                        .title(NotificationType.QUESTION_ANSWER_SELECTED.getTitle(answer.getQuestion().getTitle()))
                        .content(NotificationType.QUESTION_ANSWER_SELECTED.getContent(answer.getMember().getNickname()))
                        .data(new QuestionData(answer.getQuestion().getId()))
                        .build()
        );

        snsService.publishMessage(List.of(notification));
    }

    /* QUESTION_ANSWER_SELECTED_AUTO */
    @After(value = "execution(* kr.co.teacherforboss.scheduler.QuestionScheduler.autoSelectAnswer(..)) && args(question)")
    public void sendAutoSelectAnswerNotification(Question question) {
        log.info("===== Send Auto Select Answer Notification =====");

        Member target = question.getMember();
        if (!agreeNotification(target, NotificationType.QUESTION_ANSWER_SELECTED_AUTO)) return;

        Notification notification = notificationRepository.save(
                Notification.builder()
                        .member(target)
                        .type(NotificationType.QUESTION_ANSWER_SELECTED_AUTO)
                        .title(NotificationType.QUESTION_ANSWER_SELECTED_AUTO.getTitle(question.getTitle()))
                        .content(NotificationType.QUESTION_ANSWER_SELECTED_AUTO.getContent())
                        .data(new QuestionData(question.getId()))
                        .build()
        );

        snsService.publishMessage(List.of(notification));
    }

    /* QUESTION_ANSWER_LIKED */
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.boardService.BoardCommandService.toggleAnswerLike(..))", returning = "answerLike")
    public void sendAnswerLikedNotification(AnswerLike answerLike) {
        if (!answerLike.getLiked().isIdentifier()) return;

        log.info("===== Send Answer Liked Notification =====");

        Member target = answerLike.getAnswer().getMember();
        if (!agreeNotification(target, NotificationType.QUESTION_ANSWER_LIKED)) return;

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
    @Around("execution(* kr.co.teacherforboss.scheduler.HomeScheduler.updateHotQuestions())")
    public void sendHotQuestionNotification(ProceedingJoinPoint joinPoint) {
        log.info("===== Send Hot Question Notification =====");
        try {
            GetHotQuestionsDTO before = cacheManager.getCache(RedisConfig.HOT_QUESTION_CACHE_NAME).get(RedisConfig.HOT_QUESTION_CACHE_KEY, GetHotQuestionsDTO.class);
            GetHotQuestionsDTO after = (GetHotQuestionsDTO) joinPoint.proceed();

            List<Long> hotQuestionIds = after.getHotQuestionList().stream()
                    .filter(hotQuestionInfo -> before == null || before.getHotQuestionList().stream().noneMatch(questionInfo -> questionInfo.getQuestionId().equals(hotQuestionInfo.getQuestionId())))
                    .map(HotQuestionInfo::getQuestionId)
                    .toList();

            List<Question> hotQuestions = questionRepository.findAllById(hotQuestionIds);

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

            notifications = notifications.stream().filter(notification -> agreeNotification(notification.getMember(), notification.getType())).toList();
            notificationRepository.saveAll(notifications);
            snsService.publishMessage(notifications);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /* QUESTION_NEW */
    @Scheduled(cron = "0 0/1 * * * ?")
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
            members = memberRepository.findAllAgreeServiceNotification(PageRequest.of(page++, batchSize));

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

    /* POST_NEW_COMMENT, POST_COMMENT_NEW_REPLY */
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.boardService.BoardCommandService.saveComment(..))", returning = "comment")
    public void sendNewCommentNotification(Comment comment) {
        log.info("===== Send New Comment Notification =====");

        Member target;
        Notification notification;

        if (comment.getParent() != null) {
            target = comment.getParent().getMember();
            if (!agreeNotification(target, NotificationType.POST_COMMENT_NEW_REPLY)) return;

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
            if (!agreeNotification(target, NotificationType.POST_NEW_COMMENT)) return;

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
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.boardService.BoardQueryService.getPost(..))", returning = "getPostDTO")
    public void sendViewIncreasedNotification(JoinPoint joinPoint, BoardResponseDTO.GetPostDTO getPostDTO) {
        Long postId = (Long) joinPoint.getArgs()[0];
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return;

        if (post.getViewCount() % 50 == 0) {
            log.info("===== Send View Increased Notification =====");

            Member target = post.getMember();
            if (!agreeNotification(target, NotificationType.POST_VIEW_INCREASED)) return;

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
    @Around("execution(* kr.co.teacherforboss.scheduler.HomeScheduler.updateHotPosts())")
    public void sendHotPostNotification(ProceedingJoinPoint joinPoint) {
        log.info("===== Send Hot Post Notification =====");
        try {
            GetHotPostsDTO before = cacheManager.getCache(RedisConfig.HOT_POST_CACHE_NAME).get(RedisConfig.HOT_POST_CACHE_KEY, GetHotPostsDTO.class);
            GetHotPostsDTO after = (GetHotPostsDTO) joinPoint.proceed();

            List<Long> hotPostIds = after.getHotPostList().stream()
                    .filter(hotPostInfo -> before == null || before.getHotPostList().stream().noneMatch(postInfo -> postInfo.getPostId().equals(hotPostInfo.getPostId())))
                    .map(HotPostInfo::getPostId)
                    .toList();

            List<Post> hotPosts = postRepository.findAllById(hotPostIds);

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

            notifications = notifications.stream().filter(notification -> agreeNotification(notification.getMember(), notification.getType())).toList();
            notificationRepository.saveAll(notifications);
            snsService.publishMessage(notifications);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
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
            members = memberRepository.findAllAgreeServiceNotification(PageRequest.of(page++, batchSize));

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

        snsService.publishMessage(notifications.get(0));
    }

    /* EXCHANGE_COMPLETE */
    @AfterReturning(pointcut = "execution(* kr.co.teacherforboss.service.paymentService.PaymentCommandService.completeExchangeProcess(..))", returning = "exchange")
    public void sendExchangeCompleteNotification(Exchange exchange) {
        if (exchange.getExchangeType() != ExchangeType.EX) return;

        log.info("===== Send Exchange Complete Notification =====");

        Member target = exchange.getMember();
        if (!agreeNotification(target, NotificationType.EXCHANGE_COMPLETE)) return;
        
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

    /* TEACHER_SIGNUP_COMPLETE */
    @AfterReturning(value = "execution(* kr.co.teacherforboss.service.authService.AuthCommandService.completeTeacherSignup(..))", returning = "member")
    public void sendTeacherSignupCompleteNotification(Member member) {
        log.info("===== Send Teacher Signup Complete Notification =====");

        if (!agreeNotification(member, NotificationType.TEACHER_SIGNUP_COMPLETE)) return;

        Notification notification = notificationRepository.save(
                Notification.builder()
                        .member(member)
                        .type(NotificationType.TEACHER_SIGNUP_COMPLETE)
                        .title(NotificationType.TEACHER_SIGNUP_COMPLETE.getTitle())
                        .content(NotificationType.TEACHER_SIGNUP_COMPLETE.getContent())
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

    private boolean agreeNotification(Member target, NotificationType type) {
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(target.getId()).orElse(null);
        if (notificationSetting == null || notificationSetting.getService().equals(BooleanType.F)) return false;
        return true;
    }
}
