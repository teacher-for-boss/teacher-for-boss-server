package kr.co.teacherforboss.service.deviceTokenService;


import java.util.List;
import kr.co.teacherforboss.domain.DeviceToken;
import kr.co.teacherforboss.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceTokenQueryServiceImpl implements DeviceTokenQueryService {

    private final DeviceTokenRepository deviceTokenRepository;

    @Override
    public List<DeviceToken> getDeviceTokens(Long memberId) {
        return deviceTokenRepository.findAllByMemberId(memberId);
    }
}
