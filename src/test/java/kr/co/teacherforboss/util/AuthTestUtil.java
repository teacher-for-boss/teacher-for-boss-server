package kr.co.teacherforboss.util;

import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.PhoneAuth;
import kr.co.teacherforboss.domain.enums.Gender;
import kr.co.teacherforboss.domain.enums.LoginType;
import kr.co.teacherforboss.domain.enums.Purpose;
import kr.co.teacherforboss.domain.enums.Role;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AuthTestUtil {

    public EmailAuth generateEmailAuthDummy(String email) {
            return EmailAuth.builder()
                    .email(email)
                    .purpose(Purpose.SIGNUP)
                    .code("12345")
                    .isChecked("F")
                    .build();

    }

    public Member generateMemberDummy(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return Member.builder()
                .name("백채연")
                .email("email@gmail.com")
                .loginType(LoginType.GENERAL)
                .role(Role.USER)
                .birthDate(LocalDate.parse("2000-04-22", formatter))
                .gender(Gender.FEMALE)
                .phone("01012341234")
                .pwSalt("salt")
                .pwHash("hash")
                .build();
    }

    public PhoneAuth generatePhoneAuthDummy(){
        return PhoneAuth.builder()
                .phone("01012341234")
                .isChecked("T")
                .purpose(Purpose.of(2))
                .code("12345")
                .build();
    }

    public PhoneAuth generateNotCheckPhoneAuthDummy(){
        return PhoneAuth.builder()
                .phone("01012341234")
                .isChecked("F")
                .purpose(Purpose.of(2))
                .code("12345")
                .build();
    }
}
