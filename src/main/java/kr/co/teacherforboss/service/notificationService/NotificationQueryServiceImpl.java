package kr.co.teacherforboss.service.notificationService;

import kr.co.teacherforboss.converter.NotificationConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.NotificationSetting;
import kr.co.teacherforboss.repository.NotificationSettingRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final AuthCommandService authCommandService;
    private final NotificationSettingRepository notificationSettingRepository;
    @Override
    public NotificationResponseDTO.SettingsDTO getSettings() {
        Member member = authCommandService.getMember();
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(member.getId())
                .orElseGet(() -> notificationSettingRepository.save(NotificationSetting.of(member)));
        return NotificationConverter.toSettingsDTO(notificationSetting);
    }
}
