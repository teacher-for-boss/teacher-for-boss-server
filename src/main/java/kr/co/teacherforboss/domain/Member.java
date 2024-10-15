package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.Gender;
import kr.co.teacherforboss.domain.enums.LoginType;
import kr.co.teacherforboss.domain.enums.Role;
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
@Table(uniqueConstraints = {
        @UniqueConstraint(name= "member_email_uk", columnNames={ "email" }),
        @UniqueConstraint(name= "member_phone_uk", columnNames={ "phone" })
})
public class Member extends BaseEntity {

    @NotNull
    @Column(length = 20)
    private String name;

    @NotNull
    @Column(length = 10)
    private String nickname;

    @NotNull
    @Column(length = 100)
    private String email;

    @NotNull
    @Column(length = 40)
    private String pwSalt;

    @NotNull
    @Column(length = 64)
    private String pwHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private LoginType loginType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private Role role;

    @Column(columnDefinition = "TEXT")
    private String profileImg;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private Gender gender;

    @NotNull
    @Column(length = 50)
    private String phone;

    @Column
    private LocalDate birthDate;

    @Column
    private LocalDateTime inactiveDate;

    public void setInactiveDate(LocalDateTime inactiveDate) {
        this.inactiveDate = inactiveDate;
    }

    public void setPassword(List<String> passwordList){
        this.pwSalt = passwordList.get(0);
        this.pwHash = passwordList.get(1);
    }

    public void setProfile(String nickname, String profileImg){
        this.nickname = nickname;
        this.profileImg = profileImg;
    }

    public Member setRole(Role role) {
        this.role = role;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member that = (Member) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public boolean softDelete() {
        setInactiveDate(LocalDateTime.now());
        return super.softDelete();
    }
}