package kr.co.teacherforboss.domain.vo.notificationVO;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"  // JSON에 "type" 필드를 추가하여 어떤 서브 클래스인지 지정
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NotificationLinkData.QuestionData.class, name = "question"),
        @JsonSubTypes.Type(value = NotificationLinkData.PostData.class, name = "post")
})
public class NotificationLinkData {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionData extends NotificationLinkData {
        private Long questionId;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostData extends NotificationLinkData {
        private Long postId;
    }
}
