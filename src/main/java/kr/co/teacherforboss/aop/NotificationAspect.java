package kr.co.teacherforboss.aop;

import java.util.List;
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
import kr.co.teacherforboss.domain.vo.notificationVO.NotificationLinkData.PostData;
import kr.co.teacherforboss.domain.vo.notificationVO.NotificationLinkData.QuestionData;
import kr.co.teacherforboss.repository.NotificationRepository;
import kr.co.teacherforboss.repository.NotificationSettingRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.service.notificationService.NotificationCommandService;
import kr.co.teacherforboss.service.snsService.SnsService;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
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
    private final PostRepository postRepository;

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
        if (answerLike.getLiked() == null || !answerLike.getLiked().isIdentifier()) return;

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

//        if (!agreeNotification(member, NotificationType.TEACHER_SIGNUP_COMPLETE)) return;

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
//    @AfterReturning(value = "execution(* kr.co.teacherforboss.service.notificationService.NotificationQueryService.getNotifications(..))", returning = "notificationsDTO")
//    public void readNotification(NotificationResponseDTO.GetNotificationsDTO notificationsDTO) {
//        notificationCommandService.readNotifications(notificationsDTO.getNotificationList()
//                .stream().map(NotificationResponseDTO.GetNotificationsDTO.NotificationInfo::getNotificationId).toList());
//    }

    private boolean agreeNotification(Member target, NotificationType type) {
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(target.getId()).orElse(null);
        if (notificationSetting == null || notificationSetting.getService().equals(BooleanType.F)) return false;
        return true;
    }
}
