package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.Level;
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
public class TeacherInfo extends BaseEntity {

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @NotNull
    @Column(length = 100)
    private String businessNumber;

    @NotNull
    @Column(length = 20)
    private String representative;

    @NotNull
    @Column
    private LocalDate openDate;

    @NotNull
    @Column(length = 10)
    private String bank;

    @NotNull
    @Column(length = 32)
    private String accountNumber;

    @NotNull
    @Column(length = 20)
    private String accountHolder;

    @NotNull
    @Column(length = 20)
    private String field;

    @NotNull
    @Column
    private Integer career;

    @NotNull
    @Column(length = 40)
    private String introduction;

    @NotNull
    @Column(length = 30)
    private String keywords;

    @NotNull
    @Column(length = 10)
    private Level level;

    @Column(length = 100)
    private String email;

    @Column
    private BooleanType emailOpen;

    @Column(length = 50)
    private String phone;

    @Column
    private BooleanType phoneOpen;

    public void editTeacherInfo(String field, Integer career, String introduction, List<String> keywordsList, String email,
                                BooleanType emailOpen, String phone, BooleanType phoneOpen) {
        String keywords = String.join(";", keywordsList);
        this.field = field;
        this.career = career;
        this.introduction = introduction;
        this.keywords = keywords;
        this.email = email;
        this.emailOpen = emailOpen;
        this.phone = phone;
        this.phoneOpen = phoneOpen;
    }
}