package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.converter.NotificationLinkDataConverter;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.NotificationType;
import kr.co.teacherforboss.domain.vo.notificationVO.NotificationLinkData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Column
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column
    private String title;

    @Column
    private String content;

    @Convert(converter = NotificationLinkDataConverter.class)
    @Column(columnDefinition = "TEXT")
    private NotificationLinkData data;

    @Column
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'F'")
    @Builder.Default
    private BooleanType isRead = BooleanType.F;

    @Column
    private LocalDateTime sentAt;

    public Notification setSentAt(LocalDateTime time) {
        this.sentAt = time;
        return this;
    }
}
