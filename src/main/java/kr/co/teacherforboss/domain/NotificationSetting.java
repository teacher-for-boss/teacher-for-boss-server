package kr.co.teacherforboss.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.web.dto.NotificationRequestDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NotificationSetting {

    @Id
    private Long memberId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BooleanType service = BooleanType.T;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BooleanType marketing = BooleanType.T;

    public static NotificationSetting of(Member member) {
        return NotificationSetting.builder()
                .memberId(member.getId())
                .build();
    }

    public NotificationSetting updateSettings(NotificationRequestDTO.SettingsDTO request) {
        this.service = BooleanType.of(request.isServiceNotification());
        this.marketing = BooleanType.of(request.getMarketingNotification().isPush());
        return this;
    }
}
