package kr.co.teacherforboss.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;
import kr.co.teacherforboss.domain.enums.Status;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    private Status status = Status.ACTIVE;

    @Column(updatable = false, nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @Getter
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    @Getter
    protected LocalDateTime updatedAt;

    public boolean softDelete() {
        if (status == Status.ACTIVE)
            throw new GeneralException(ErrorStatus.ALREADY_DELETED);
        this.status = Status.INACTIVE;
        return true;
    }
}
