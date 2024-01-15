package kr.co.teacherforboss.service.AuthService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.Gender;
import kr.co.teacherforboss.domain.enums.LoginType;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Handler;

import static kr.co.teacherforboss.util.ValidationRegex.isRegexPassword;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class AuthCommandServiceImplTest {

    @InjectMocks
    private AuthCommandServiceImpl authCommandService;

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @DisplayName("회원 가입 (성공)")
    @Test
    void joinMember() {
        // given
        Member member = member(); // 임의로 생성한 Member
        String salt = authCommandService.getSalt();
        String encryptedPw = passwordEncoder.encode(salt+"비밀번호");
        member.setPassword(salt, encryptedPw);

        AuthRequestDTO.JoinDto request = request("백채연", "email@gmail.com", "asdf1234", "asdf1234", "T", 2); // request로 입력한 Member data

        doReturn(member).when(memberRepository) // Mock 객체 반환값 설정
                .save(any(Member.class));

        // when
        Member savedMember = authCommandService.joinMember(request);

        // then
        assertThat(savedMember.getEmail()).isEqualTo(member.getEmail());
        assertThat(savedMember.getRole()).isEqualTo(member.getRole());

        // verify
        verify(memberRepository, times(1)).save(any(Member.class));

    }

    @DisplayName("회원 가입 (실패) - 이메일 인증")
    @Test
    void signup_failed_email() {
        // when
        AuthRequestDTO.JoinDto request = request("백채연", "email@gmail.com", "asdf1234", "asdf1234", "F", 2); // request로 입력한 Member data

        // given
        GeneralException exception = assertThrows(GeneralException.class, () -> {
            authCommandService.joinMember(request);
        });

        // then
        assertEquals(ErrorStatus.EMAIL_NOT_CHECKED, exception.getCode());


    }

    @DisplayName("회원 가입 (실패) - 비밀번호 정규식")
    @Test
    void signup_failed_pw_validation() {
        // given
        AuthRequestDTO.JoinDto request1 = request("백채연1", "email@gmail.com", "asdfasdf", "asdfasdf", "T", 2);
        AuthRequestDTO.JoinDto request2 = request("백채연2", "email@gmail.com", "asdfasdf1234123412341234", "asdfasdf1234123412341234", "T", 2);
        AuthRequestDTO.JoinDto request3 = request("백채연3", "email@gmail.com", "asdfasdfasdf", "asdfasdf12341234", "T", 2);
        AuthRequestDTO.JoinDto request4 = request("백채연4", "email@gmail.com", "123412341234", "123412341234", "T", 2);
        AuthRequestDTO.JoinDto request5 = request("백채연5", "email@gmail.com", "asdf1234", "asdf1234", "T", 2); // 정상값

        // when
        boolean result1 = isRegexPassword(request1.getPassword());
        boolean result2 = isRegexPassword(request2.getPassword());
        boolean result3 = isRegexPassword(request3.getPassword());
        boolean result4 = isRegexPassword(request4.getPassword());
        boolean result5 = isRegexPassword(request5.getPassword());

        // then
        assertThat(result1).isFalse();
        assertThat(result2).isFalse();
        assertThat(result3).isFalse();
        assertThat(result4).isFalse();
        assertThat(result5).isTrue();

    }

    @DisplayName("회원 가입 (실패) - 비밀번호 암호화")
    @Test
    void signup_failed_password_encypt() {
        // given
        Member member = member(); // 임의로 생성한 Member
        String salt = authCommandService.getSalt();
        String encryptedPw = passwordEncoder.encode(salt+"asdf1234");
        member.setPassword(salt, encryptedPw);

        AuthRequestDTO.JoinDto request = request("백채연", "email@gmail.com", "asdf1234", "asdf1234", "T", 2); // request로 입력한 Member data

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // when
        Member savedMember = authCommandService.joinMember(request);

        // then
        String pwSalt = passwordEncoder.encode(savedMember.getPwSalt()+"asdf1234"); // 비교대상 pwSalt값
        assertEquals(pwSalt, savedMember.getPwHash());
    }


    private Member member(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return Member.builder()
                .name("백채연")
                .email("email@gmail.com")
                .loginType(LoginType.GENERAL)
                .role(Role.USER)
                .birthDate(LocalDate.parse("2000-04-22", formatter))
                .gender(Gender.FEMALE)
                .build();
    }

    private AuthRequestDTO.JoinDto request(String name, String email, String pw, String rePw, String isChecked, Integer gender){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return AuthRequestDTO.JoinDto.builder()
                .name(name)
                .email(email)
                .password(pw)
                .rePassword(rePw)
                .birthDate(LocalDate.parse("2000-04-22", formatter))
                .isChecked(isChecked)
                .gender(gender)
                .build();
    }

}