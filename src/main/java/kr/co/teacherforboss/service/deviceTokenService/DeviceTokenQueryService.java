package kr.co.teacherforboss.service.deviceTokenService;

import java.util.List;
import kr.co.teacherforboss.domain.DeviceToken;

public interface DeviceTokenQueryService {
    List<DeviceToken> getDeviceTokens(Long memberId);
}
