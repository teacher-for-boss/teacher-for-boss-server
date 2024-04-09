package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.Gender;
import kr.co.teacherforboss.domain.enums.LoginType;
import kr.co.teacherforboss.domain.enums.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @NotNull
    @Column(length = 20)
    private String name;

    @NotNull
    @Column(length = 100, unique = true)
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
    @Column(length = 50, unique = true)
    private String phone;

    @Column
    private LocalDate birthDate;

    @Column
    private LocalDate inactiveDate;

    public void setPassword(String pwSalt, String pwHash){
        this.pwSalt = pwSalt;
        this.pwHash = pwHash;
    }

}