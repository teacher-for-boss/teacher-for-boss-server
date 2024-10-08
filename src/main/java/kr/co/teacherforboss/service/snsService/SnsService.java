package kr.co.teacherforboss.service.snsService;

import java.util.List;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Notification;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;

public interface SnsService {
    void createEndpoint(Member member, AuthRequestDTO.DeviceInfoDTO deviceInfoDTO);
    void deleteEndpoint(Member member);
    void deleteEndpoint(Member member, String fcmToken);
    void publishMessage(String message);
    void publishMessage(String message, List<Member> targetMembers);
    void publishMessage(List<Notification> notifications);
}
