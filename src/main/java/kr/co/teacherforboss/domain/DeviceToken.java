package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.NotificationTopic;
import kr.co.teacherforboss.web.dto.AuthRequestDTO.DeviceInfoDTO;
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
public class DeviceToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Column
    private String fcmToken;

    @Column
    private String endpointArn;

    @Column
    private String subscriptionArn;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationTopic topic;

    @Column
    private String platform;

    public void updateEndpointArn(String endpointArn) {
        this.endpointArn = endpointArn;
    }

    public void updateSubscriptionArn(String subscriptionArn) {
        this.subscriptionArn = subscriptionArn;
    }

    public static DeviceInfoDTO toDeviceInfoDTO(DeviceToken deviceToken) {
        return DeviceInfoDTO.builder()
                .fcmToken(deviceToken.getFcmToken())
                .platform(deviceToken.getPlatform())
                .build();
    }
}
