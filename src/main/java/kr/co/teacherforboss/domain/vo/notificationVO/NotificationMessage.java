package kr.co.teacherforboss.domain.vo.notificationVO;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;
import kr.co.teacherforboss.domain.Notification;
import kr.co.teacherforboss.domain.enums.NotificationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationMessage {
    private String title;
    private String body;
    private NotificationType notificationType;
    private NotificationLinkData notificationLinkData;

    public String getMessage() {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("body", body);

        Map<String, Object> data = new HashMap<>();
        data.put("notificationType", notificationType);
        data.put("notificationLinkData", notificationLinkData);

        Map<String, Object> message = new HashMap<>();
        message.put("notification", notification);
        message.put("data", data);

        Map<String, Object> messagePayload = new HashMap<>();
        messagePayload.put("default", message);
        messagePayload.put("GCM", message);

        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.SNS_JSON_PARSE_FAIL);
        }
    }

    public static NotificationMessage from(Notification notification) {
        return NotificationMessage.builder()
                .title(notification.getTitle())
                .body(notification.getContent())
                .notificationType(notification.getType())
                .notificationLinkData(notification.getData())
                .build();
    }
}
