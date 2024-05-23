package kr.co.teacherforboss.service.authService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;
import kr.co.teacherforboss.domain.AgreementTerm;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.PhoneAuth;
import kr.co.teacherforboss.domain.enums.Purpose;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.domain.vo.mailVO.CodeMail;
import kr.co.teacherforboss.domain.vo.mailVO.Mail;
import kr.co.teacherforboss.repository.AgreementTermRepository;
import kr.co.teacherforboss.repository.EmailAuthRepository;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.PhoneAuthRepository;
import kr.co.teacherforboss.service.mailService.MailCommandService;
import kr.co.teacherforboss.util.AuthTestUtil;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.query.sqm.tree.SqmNode.log;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthCommandServiceImplTest {

    @InjectMocks @Spy
    private AuthCommandServiceImpl authCommandService;
    @Mock
    private MailCommandService mailCommandService;
    @Mock
    private EmailAuthRepository emailAuthRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PhoneAuthRepository phoneAuthRepository;
    @Mock
    private AgreementTermRepository agreementTermRepository;
    @Mock
    private CodeMail codeMail;
    @InjectMocks
    private AuthTestUtil authTestUtil;

    /*
    // TODO: 이메일 인증 테스트
     */
    @DisplayName("이메일 인증 (성공)")
    @Test
    void saveEmailAuth() {
        // given
        AuthRequestDTO.SendCodeMailDTO request = makeRequest("test@gmail.com", 1);
        EmailAuth expected = authTestUtil.generateEmailAuthDummy(request.getEmail());
        Map<String, String> code = new HashMap<>();
        code.put("code", "12345");

        doReturn(request.getEmail()).when(mailCommandService).sendMail(any(String.class), any(Mail.class));
        lenient().doReturn(code).when(codeMail).getValues();
        doReturn(expected).when(emailAuthRepository).save(any(EmailAuth.class));

        // when
        EmailAuth result = authCommandService.sendCodeMail(request);

        // then
        assertThat(result.getEmail()).isEqualTo(expected.getEmail());
        assertThat(result.getPurpose()).isEqualTo(expected.getPurpose());
        assertThat(result.getCode()).isEqualTo(expected.getCode());
        assertThat(result.getIsChecked()).isEqualTo(expected.getIsChecked());

        // verify
        verify(emailAuthRepository, times(1)).save(any(EmailAuth.class));
        verify(mailCommandService, times(1)).sendMail(any(String.class), any(Mail.class));
    }

    //TODO: 하루 가능 이메일 인증 횟수 초과 시 실패 테스트

    private AuthRequestDTO.SendCodeMailDTO makeRequest(String email, int purpose) {
        return AuthRequestDTO.SendCodeMailDTO.builder()
                .email(email)
                .purpose(purpose)
                .build();
    }

    /*
    // TODO: 회원 가입 테스트
     */
    @DisplayName("회원 가입 (성공) - 비밀번호 검증 제외")
    @Test
    void joinMember() {
        // given
        Member expected = authTestUtil.generateMemberDummy("email@gmail.com");
        AuthRequestDTO.JoinDTO request = request("백채연", "email@gmail.com", "asdf1234", "asdf1234", 2, "01012341234", "T"); // request로 입력한 Member data
        AgreementTerm mockAgreement = authTestUtil.generateAgreementTerm();
        doReturn(expected).when(memberRepository)
                .save(any(Member.class));
        doReturn(mockAgreement).when(agreementTermRepository)
                .save(any(AgreementTerm.class));

        // when
        Member savedMember = authCommandService.joinMember(request);

        // then
        assertThat(expected.getEmail()).isEqualTo(savedMember.getEmail());
        assertThat(expected.getName()).isEqualTo(savedMember.getName());

        // verify
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @DisplayName("회원 가입 (실패) - 필수 약관동의 체크 X")
    @Test
    void failJoinMemberWithAgreement() {
        // given
        AuthRequestDTO.JoinDTO request = request("백채연", "email@gmail.com", "asdf1234", "asdf1234", 2, "01012341234", "F");

        // when
        GeneralException e = assertThrows(GeneralException.class
                , () -> authCommandService.joinMember(request));

        // then
        assertThat(e.getCode()).isEqualTo(ErrorStatus.INVALID_AGREEMENT_TERM);
    }

    @Test
    @DisplayName("회원 가입 - 동시성 테스트")
    void joinMemberWithConcurrent() throws InterruptedException {
        // given
        Member expected = authTestUtil.generateMemberDummy("email@gmail.com");
        AuthRequestDTO.JoinDTO request = request("백채연", "email@gmail.com", "asdf1234", "asdf1234", 2, "01012341234", "T"); // request로 입력한 Member data

        doReturn(expected).when(memberRepository)
                .save(any(Member.class));

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);

        // when
        log.info("회원가입 동시성 테스트 진행");
        ArrayList<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                Member member = memberRepository.save(authCommandService.joinMember(request));
                if (memberList.isEmpty()){
                    memberList.add(member);
                }
                else{
                    for (Member m : memberList) {
                        if (!m.getEmail().equals(member.getEmail())) {
                            memberList.add(member);
                        }
                    }
                }
                latch.countDown();
            });
        }
        latch.await();

        // then
        assertEquals(memberList.size(), 1);
    }

    private AuthRequestDTO.JoinDTO request(String name, String email, String pw, String rePw, Integer gender, String phone, String agreement){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return AuthRequestDTO.JoinDTO.builder()
                .name(name)
                .email(email)
                .password(pw)
                .rePassword(rePw)
                .birthDate(LocalDate.parse("2000-04-22", formatter))
                .phone(phone)
                .gender(gender)
                .emailAuthId(1L)
                .phoneAuthId(1L)
                .agreementAge(agreement)
                .agreementUsage(agreement)
                .agreementInfo(agreement)
                .build();
    }

    /*
    // TODO: 이메일 찾기 테스트
     */
    @DisplayName("이메일 찾기 (성공)")
    @Test
    void findEmail() {
        // given
        PhoneAuth phoneAuth = authTestUtil.generatePhoneAuthDummy();
        Member member = authTestUtil.generateMemberDummy("email@gmail.com");

        AuthRequestDTO.FindEmailDTO request = toFindEmail(1L);
        doReturn(Optional.of(phoneAuth)).when(phoneAuthRepository).findById(any(Long.class));
        doReturn(true).when(phoneAuthRepository)
                .existsByIdAndPurposeAndIsChecked(any(Long.class), any(Purpose.class), any(String.class));
        doReturn(member).when(memberRepository).findByPhoneAndStatus(any(String.class), any(Status.class));

        // when
        Member result = authCommandService.findEmail(request);

        // then
        assertThat(result.getEmail()).isEqualTo(member.getEmail());

        verify(memberRepository, times(1)).findByPhoneAndStatus(any(String.class), any(Status.class));

    }

    @DisplayName("이메일 찾기 (실패) - 이메일 인증 X")
    @Test
    void failedFindEmail() {
        // given
        PhoneAuth phoneAuth = authTestUtil.generateNotCheckPhoneAuthDummy(); // 인증 X 인 PhoneAuth
        AuthRequestDTO.FindEmailDTO request = toFindEmail(1L);
        doReturn(Optional.of(phoneAuth)).when(phoneAuthRepository).findById(any(Long.class)); // PhoneAuth F 저장

        // when
        GeneralException e = assertThrows(GeneralException.class
                , () -> authCommandService.findEmail(request));

        // then
        assertThat(e.getCode()).isEqualTo(ErrorStatus.PHONE_NOT_CHECKED);

        verify(memberRepository, times(0)).findByPhoneAndStatus(any(String.class), any(Status.class));

    }

    private AuthRequestDTO.FindEmailDTO toFindEmail(Long phoneAuthId){
        return AuthRequestDTO.FindEmailDTO.builder()
                .phoneAuthId(phoneAuthId)
                .build();
    }

    /*
    // TODO: 비밀번호 찾기 테스트
     */
    @DisplayName("비밀번호 찾기 (성공)")
    @Test
    void findPassword() {
        // given
        EmailAuth emailAuth = authTestUtil.generateFindPwCheckEmailAuthDummy("email@gmail.com");
        Member member = authTestUtil.generateMemberDummy("email@gmail.com");
        AuthRequestDTO.FindPasswordDTO request = toFindPassword(1L);

        doReturn(Optional.of(emailAuth)).when(emailAuthRepository).findById(anyLong());
        doReturn(true).when(emailAuthRepository)
                .existsByIdAndPurposeAndIsChecked(anyLong(), any(Purpose.class), anyString());
        doReturn(Optional.of(member)).when(memberRepository).findByEmailAndStatus(any(String.class), any(Status.class));

        // when
        Member result = authCommandService.findPassword(request);

        // then
        assertThat(result.getEmail()).isEqualTo(member.getEmail());

        verify(memberRepository, times(1)).findByEmailAndStatus(any(String.class), any(Status.class));
    }

    @DisplayName("비밀번호 찾기 (실패) - 이메일 인증 X")
    @Test
    void failFindPassword() {
        // given
        EmailAuth emailAuth = authTestUtil.generateFindPwNotCheckEmailAuthDummy("test@test.com");
        AuthRequestDTO.FindPasswordDTO request = toFindPassword(1L);
        doReturn(Optional.of(emailAuth)).when(emailAuthRepository).findById(any(Long.class));

        // when
        GeneralException e = assertThrows(GeneralException.class
                , () -> authCommandService.findPassword(request));

        // then
        assertThat(e.getCode()).isEqualTo(ErrorStatus.PHONE_NOT_CHECKED);

        verify(memberRepository, times(0)).findByEmailAndStatus(any(String.class), any(Status.class));
    }

    private AuthRequestDTO.FindPasswordDTO toFindPassword(Long emailAuthId){
        return AuthRequestDTO.FindPasswordDTO.builder()
                .emailAuthId(emailAuthId)
                .build();
    }
}
