package kr.co.teacherforboss.service.AuthService;

import java.util.HashMap;
import java.util.Map;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.enums.Purpose;
import kr.co.teacherforboss.domain.vo.mailVO.CodeMail;
import kr.co.teacherforboss.domain.vo.mailVO.Mail;
import kr.co.teacherforboss.repository.EmailAuthRepository;
import kr.co.teacherforboss.service.MailService.MailCommandService;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthCommandServiceImplTest {

    @InjectMocks
    private AuthCommandServiceImpl authCommandService;

    @Mock
    private MailCommandService mailCommandService;
    @Mock
    private EmailAuthRepository emailAuthRepository;
    @Mock
    private CodeMail codeMail;

    @DisplayName("이메일 인증 (성공)")
    @Test
    void saveEmailAuth() {
        // given
        AuthRequestDTO.SendCodeMailDTO request = makeRequest("test@gmail.com", 1);
        EmailAuth expected = makeEmailAuth(request.getEmail());
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
        return new AuthRequestDTO.SendCodeMailDTO(email, purpose);
    }

    private EmailAuth makeEmailAuth(String email) {
        return EmailAuth.builder()
                .email(email)
                .purpose(Purpose.SIGNUP)
                .code("12345")
                .isChecked("F")
                .build();
    }
}
