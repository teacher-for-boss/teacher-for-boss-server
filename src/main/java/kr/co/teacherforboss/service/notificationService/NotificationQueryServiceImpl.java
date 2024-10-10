package kr.co.teacherforboss.service.notificationService;

import java.util.List;
import kr.co.teacherforboss.converter.NotificationConverter;
import kr.co.teacherforboss.domain.AgreementTerm;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Notification;
import kr.co.teacherforboss.domain.NotificationSetting;
import kr.co.teacherforboss.repository.AgreementTermRepository;
import kr.co.teacherforboss.repository.NotificationRepository;
import kr.co.teacherforboss.repository.NotificationSettingRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final AuthCommandService authCommandService;
    private final NotificationSettingRepository notificationSettingRepository;
    private final NotificationRepository notificationRepository;
    private final AgreementTermRepository agreementTermRepository;
    @Override
    public NotificationResponseDTO.SettingsDTO getSettings() {
        Member member = authCommandService.getMember();
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(member.getId())
                .orElseGet(() -> notificationSettingRepository.save(NotificationSetting.of(member)));
        AgreementTerm agreementTerm = agreementTermRepository.findByMemberId(member.getId())
                .orElseGet(() -> agreementTermRepository.save(AgreementTerm.of(member)));

        return NotificationConverter.toSettingsDTO(notificationSetting, agreementTerm);
    }

    @Override
    public NotificationResponseDTO.GetNotificationsDTO getNotifications(Long lastNotificationId, int size) {
        Member member = authCommandService.getMember();
        PageRequest.of(0, size);
        Slice<Notification> notifications;

        if (lastNotificationId == 0) {
            notifications = notificationRepository.findByMemberIdOrderByIdDesc(member.getId(), PageRequest.of(0, size));
        }
        else {
            notifications = notificationRepository.findByMemberIdAndIdLessThanOrderByIdDesc(member.getId(), lastNotificationId, PageRequest.of(0, size));
        }

        return NotificationConverter.toGetNotificationsDTO(notifications);
    }
}
