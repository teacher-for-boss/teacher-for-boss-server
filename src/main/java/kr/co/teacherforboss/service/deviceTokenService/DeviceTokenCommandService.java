package kr.co.teacherforboss.service.deviceTokenService;

import kr.co.teacherforboss.domain.DeviceToken;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.NotificationTopic;
import kr.co.teacherforboss.web.dto.AuthRequestDTO.DeviceInfoDTO;

public interface DeviceTokenCommandService {
    DeviceToken saveDeviceToken(Member member, DeviceInfoDTO deviceInfoDTO, NotificationTopic topic);
}
