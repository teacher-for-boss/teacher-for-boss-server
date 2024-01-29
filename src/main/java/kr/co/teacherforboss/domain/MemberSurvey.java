package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.converter.SurveyListConverter;
import kr.co.teacherforboss.domain.enums.Survey;
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
public class MemberSurvey extends BaseEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    Survey question1;

    @NotNull
    @Convert(converter = SurveyListConverter.class)
    @Column(columnDefinition = "VARCHAR(70)")
    List<Survey> question2;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    Survey question3;

    @Column(columnDefinition = "VARCHAR(300)")
    String question4;

}
