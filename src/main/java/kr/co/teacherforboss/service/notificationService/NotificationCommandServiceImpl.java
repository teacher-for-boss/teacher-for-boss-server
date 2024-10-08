package kr.co.teacherforboss.service.notificationService;

import kr.co.teacherforboss.converter.NotificationConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.NotificationSetting;
import kr.co.teacherforboss.repository.NotificationSettingRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.NotificationRequestDTO;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final AuthCommandService authCommandService;
    private final NotificationSettingRepository notificationSettingRepository;

    @Override
    @Transactional
    public NotificationResponseDTO.SettingsDTO updateSettings(NotificationRequestDTO.SettingsDTO request) {
        Member member = authCommandService.getMember();
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(member.getId())
                .orElseGet(() -> notificationSettingRepository.save(NotificationSetting.of(member)));

        return NotificationConverter.toSettingsDTO(notificationSetting.updateSettings(request));
    }
}
