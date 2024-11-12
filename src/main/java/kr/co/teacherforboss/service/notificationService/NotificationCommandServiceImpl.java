package kr.co.teacherforboss.service.notificationService;

import java.util.List;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.converter.NotificationConverter;
import kr.co.teacherforboss.domain.AgreementTerm;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.NotificationSetting;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.AgreementTermRepository;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.NotificationRepository;
import kr.co.teacherforboss.repository.NotificationSettingRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.service.memberService.MemberCommandService;
import kr.co.teacherforboss.service.memberService.MemberQueryService;
import kr.co.teacherforboss.web.dto.NotificationRequestDTO;
import kr.co.teacherforboss.web.dto.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final AuthCommandService authCommandService;
    private final MemberRepository memberRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final NotificationRepository notificationRepository;
    private final AgreementTermRepository agreementTermRepository;

    @Override
    @Transactional
    public NotificationResponseDTO.SettingsDTO updateSettings(NotificationRequestDTO.SettingsDTO request) {
        Member member = authCommandService.getMember();
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(member.getId())
                .orElseGet(() -> notificationSettingRepository.save(NotificationSetting.of(member)));
        AgreementTerm agreementTerm = agreementTermRepository.findByMemberId(member.getId())
                .orElseGet(() -> agreementTermRepository.save(AgreementTerm.of(member)));

        notificationSetting.updateSettings(request);
        agreementTerm.updateAgreements(request.getMarketingNotification().isSms(), request.getMarketingNotification().isEmail());

        return NotificationConverter.toSettingsDTO(notificationSetting, agreementTerm);
    }

    @Override
    @Transactional
    public NotificationResponseDTO.SettingsDTO updateSettings(Long memberId, NotificationRequestDTO.SettingsDTO request) {
        Member member = memberRepository.findByIdAndStatus(memberId, Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        NotificationSetting notificationSetting = notificationSettingRepository.findByMemberId(member.getId())
                .orElseGet(() -> notificationSettingRepository.save(NotificationSetting.of(member)));
        AgreementTerm agreementTerm = agreementTermRepository.findByMemberId(member.getId())
                .orElseGet(() -> agreementTermRepository.save(AgreementTerm.of(member)));

        notificationSetting.updateSettings(request);
        agreementTerm.updateAgreements(request.getMarketingNotification().isSms(), request.getMarketingNotification().isEmail());

        return NotificationConverter.toSettingsDTO(notificationSetting, agreementTerm);
    }

    @Override
    @Transactional
    public void readNotifications(List<Long> notificationIds) {
        notificationRepository.readAllByIds(notificationIds);
    }
}
