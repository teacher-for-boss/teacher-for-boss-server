package kr.co.teacherforboss.domain.enums;

import java.text.MessageFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    QUESTION_NEW_ANSWER("{0}", "{0}님이 답변을 남겼어요!"),
    QUESTION_WAITING_ANSWER("{0}", "채택을 기다리는 답변이 있어요."),
    QUESTION_LAST_DAY_SELECT_ANSWER("{0}", "오늘이 답변을 채택할 수 있는 마지막 날이에요"),
    QUESTION_ANSWER_SELECTED_AUTO("{0}", "채택 기간이 지나 가장 좋은 반응을 얻은 답변이 자동 채택되었어요!"),
    QUESTION_AUTO_DELETE_ALERT("{0}", "7일 동안 답변이 없는 게시글은 삭제되며, 질문권은 자동으로 환불돼요."),
    QUESTION_AUTO_DELETE("{0}", "질문에 7일 동안 답변이 달리지 않아 삭제됐어요."),
    QUESTION_ANSWER_SELECTED("{0}", "{0}님의 답변이 채택되었어요!"),
    QUESTION_ANSWER_LIKED("{0}", "{0}님의 질문에 남긴 답변이 추천을 받았어요!"),
    QUESTION_HOT("{0}", "{0}님의 질문이 인기글이 되었어요!"),
    POST_NEW_COMMENT("{0}", "{0}님이 댓글을 남겼어요."),
    POST_COMMENT_NEW_REPLY("{0}", "{0}님이 답글을 남겼어요."),
    POST_VIEW_INCREASED("{0}", "글이 조회수 {0}명을 돌파했어요!"),
    POST_HOT("{0}", "{0}님의 글이 인기글이 되었어요!"),
    HOME_NEW_HOT_TEACHERS("보스들의 선택은 누구?", "이번 주 인기 티처 결과가 발표되었어요!"),
    EXCHANGE_COMPLETE("환전 완료!", "{0}TP 환전이 완료되었어요! 계좌를 확인해주세요."),
    TEACHER_SIGNUP_COMPLETE("티처 심사 완료!", "이제 티처포보스에서 티처로 활동할 수 있어요."),
    TEACHER_SIGNUP_REJECT("티처 심사가 반려되었어요.", "티처 심사에 실패했어요. 궁금한 점은 티처포보스로 문의해주세요."),
    NOTICE("{0}", "{0}"),
    EVENT("{0}", "{0}");

    private final String title;
    private final String content;

    public String getTitle(String... args) {
        return MessageFormat.format(this.title, (Object[]) args);
    }

    public String getContent(String... args) {
        return MessageFormat.format(this.content, (Object[]) args);
    }
}
