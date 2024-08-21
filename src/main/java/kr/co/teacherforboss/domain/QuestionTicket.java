package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.HistoryType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionTicket extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @NotNull
    @Enumerated(EnumType.STRING)
    private HistoryType historyType;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "paymentId")
//    private Payment payment;

    @NotNull
    @Column
    private Integer count = 0;

    @NotNull
    @Column
    private Integer totalCount = 0;

    @NotNull
    @Column
    private Integer questionTicketCount = 0;
}
