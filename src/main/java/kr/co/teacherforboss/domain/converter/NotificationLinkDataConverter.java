package kr.co.teacherforboss.domain.converter;

import kr.co.teacherforboss.domain.vo.notificationVO.NotificationLinkData;

public class NotificationLinkDataConverter extends JsonConverter<NotificationLinkData> {
    public NotificationLinkDataConverter() {
        super(NotificationLinkData.class);
    }
}
