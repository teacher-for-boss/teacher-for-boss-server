package kr.co.teacherforboss.service.snsService;

import java.util.List;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Notification;
import kr.co.teacherforboss.domain.enums.NotificationTopic;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;

public interface SnsService {
    void createEndpoint(Member member, List<AuthRequestDTO.DeviceInfoDTO> deviceInfoDTO, NotificationTopic topicARN);
    void deleteEndpoint(Member member);
    void deleteEndpoint(Member member, List<String> fcmTokens, NotificationTopic topicARN);
    void deleteEndpoint(Member member, String fcmToken, NotificationTopic topicARN);
    void publishMessage(String message);
    void publishMessage(Notification notification);
    void publishMessage(String message, List<Member> targetMembers);
    void publishMessage(List<Notification> notifications);
}
