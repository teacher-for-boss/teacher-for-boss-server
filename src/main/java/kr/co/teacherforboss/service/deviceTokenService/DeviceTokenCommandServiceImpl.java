package kr.co.teacherforboss.service.deviceTokenService;

import kr.co.teacherforboss.domain.DeviceToken;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.NotificationTopic;
import kr.co.teacherforboss.repository.DeviceTokenRepository;
import kr.co.teacherforboss.web.dto.AuthRequestDTO.DeviceInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceTokenCommandServiceImpl implements DeviceTokenCommandService {

    private final DeviceTokenRepository deviceTokenRepository;

    @Override
    public DeviceToken saveDeviceToken(Member member, DeviceInfoDTO deviceInfoDTO, NotificationTopic topic) {
        DeviceToken newDeviceToken = deviceTokenRepository.save(
                DeviceToken.builder()
                        .member(member)
                        .fcmToken(deviceInfoDTO.getFcmToken())
                        .platform(deviceInfoDTO.getPlatform())
                        .topic(topic)
                        .build());
        newDeviceToken.softDelete();
        return newDeviceToken;
    }
}
