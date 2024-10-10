package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.co.teacherforboss.domain.common.BaseEntity;
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
    private String platform;

    public void updateEndpointArn(String endpointArn) {
        this.endpointArn = endpointArn;
    }

    public void updateSubscriptionArn(String subscriptionArn) {
        this.subscriptionArn = subscriptionArn;
    }
}
