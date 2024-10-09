package kr.co.teacherforboss.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.domain.common.BaseEntity;
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
public class AgreementTerm extends BaseEntity {

    // TODO: 여기도 memberId를 PK로 수정하기 .. ?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    // TODO: 여기 String 말고 BooleanType으로 수정하기
    @NotNull
    @Enumerated(EnumType.STRING)
    private BooleanType agreementSms;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BooleanType agreementEmail;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BooleanType agreementLocation;

    public static AgreementTerm of(Member member) {
        return AgreementTerm.builder()
                .member(member)
                .agreementSms(BooleanType.F)
                .agreementEmail(BooleanType.F)
                .agreementLocation(BooleanType.F)
                .build();
    }

    public AgreementTerm updateAgreements(boolean agreementSms, boolean agreementEmail) {
        this.agreementSms = BooleanType.of(agreementSms);
        this.agreementEmail = BooleanType.of(agreementEmail);
        return this;
    }

}
