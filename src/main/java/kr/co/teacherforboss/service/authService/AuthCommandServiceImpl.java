package kr.co.teacherforboss.service.authService;

import java.time.Duration;
import java.time.LocalDateTime;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.config.jwt.TokenManager;
import kr.co.teacherforboss.converter.AuthConverter;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.LoginType;
import kr.co.teacherforboss.domain.AgreementTerm;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.repository.AgreementTermRepository;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
import kr.co.teacherforboss.util.PasswordUtil;
import kr.co.teacherforboss.domain.PhoneAuth;
import kr.co.teacherforboss.domain.enums.Purpose;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.PhoneAuthRepository;
import kr.co.teacherforboss.util.SecurityUtil;
import kr.co.teacherforboss.domain.vo.smsVO.CodeSMS;
import kr.co.teacherforboss.util.SmsUtil;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.vo.mailVO.CodeMail;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.EmailAuthRepository;
import kr.co.teacherforboss.service.mailService.MailCommandService;
import kr.co.teacherforboss.web.dto.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PhoneAuthRepository phoneAuthRepository;
    private final AgreementTermRepository agreementTermRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final MailCommandService mailCommandService;
    private final PasswordEncoder passwordEncoder;
    private final TokenManager tokenManager;
    private final SmsUtil smsUtil;
    private final PasswordUtil passwordUtil;

    // 회원 가입
    @Override
    @Transactional
    public Member joinMember(AuthRequestDTO.JoinDTO request){
        if (memberRepository.existsByEmailAndStatus(request.getEmail(), Status.ACTIVE))
            throw new AuthHandler(ErrorStatus.MEMBER_EMAIL_DUPLICATE);
        if (memberRepository.existsByPhoneAndStatus(request.getPhone(), Status.ACTIVE))
            throw new AuthHandler(ErrorStatus.MEMBER_PHONE_DUPLICATE);
        if (!request.getPassword().equals(request.getRePassword()))
            throw new AuthHandler(ErrorStatus.PASSWORD_NOT_CORRECT);
        if (!(request.getAgreementUsage().equals("T") && request.getAgreementInfo().equals("T") && request.getAgreementAge().equals("T")))
            throw new AuthHandler(ErrorStatus.INVALID_AGREEMENT_TERM);

        Member newMember = AuthConverter.toMember(request);
        passwordUtil.setMemberPassword(newMember, request.getPassword());

        AgreementTerm newAgreement = AuthConverter.toAgreementTerm(request, newMember);

        if (memberRepository.existsByNicknameAndStatus(request.getNickname(), Status.ACTIVE))
            throw new AuthHandler(ErrorStatus.MEMBER_NICKNAME_DUPLICATE);

        if (request.getRole().equals(1)) enterBossInfo(request, newMember);
        else if (request.getRole().equals(2)) enterTeacherInfo(request, newMember);

        agreementTermRepository.save(newAgreement);
        return memberRepository.save(newMember);
    }

    @Override
    @Transactional
    public EmailAuth sendCodeMail(AuthRequestDTO.SendCodeMailDTO request) {
        String to = request.getEmail();

        if (Purpose.of(request.getPurpose()).equals(Purpose.SIGNUP) &&
                memberRepository.existsByEmailAndStatus(request.getEmail(), Status.ACTIVE))
            throw new AuthHandler(ErrorStatus.AUTH_EMAIL_DUPLICATED);

        // TODO: 하루 이메일 인증 5회 제한 확인

        CodeMail codeMail = new CodeMail();
        mailCommandService.sendMail(to, codeMail);

        EmailAuth emailAuth = AuthConverter.toEmailAuth(request);
        emailAuth.setCode(codeMail.getValues().get("code"));

        return emailAuthRepository.save(emailAuth);
    }

    @Override
    @Transactional
    public boolean checkCodeMail(AuthRequestDTO.CheckCodeMailDTO request) {

        EmailAuth emailAuth = emailAuthRepository.findById(request.getEmailAuthId())
                .orElseThrow(() -> new AuthHandler(ErrorStatus._DATA_NOT_FOUND));

        boolean codeCheck = request.getEmailAuthCode().equals(emailAuth.getCode());
        if (!codeCheck) throw new AuthHandler(ErrorStatus.INVALID_CODE_MAIL);

        boolean timeCheck = Duration.between(emailAuth.getCreatedAt(), LocalDateTime.now()).getSeconds() < CodeMail.VALID_TIME;
        if (!timeCheck) throw new AuthHandler(ErrorStatus.TIMEOUT_CODE_MAIL);

        emailAuth.setIsChecked(true);
        return true;
    }

    @Override
    @Transactional
    public PhoneAuth sendCodePhone(AuthRequestDTO.SendCodePhoneDTO request) {
        String to = request.getPhone();

        if (Purpose.of(request.getPurpose()).equals(Purpose.SIGNUP) &&
                memberRepository.existsByPhoneAndStatus(request.getPhone(), Status.ACTIVE))
            throw new AuthHandler(ErrorStatus.AUTH_PHONE_DUPLICATED);

        // TODO: 하루 휴대폰 인증 5회 제한 확인

        CodeSMS codeSMS = new CodeSMS(request.getAppHash());
        smsUtil.sendOne(to, codeSMS);

        PhoneAuth phoneAuth = AuthConverter.toPhoneAuth(request);
        phoneAuth.setCode(codeSMS.getCode());

        return phoneAuthRepository.save(phoneAuth);
    }

    @Override
    @Transactional
    public boolean checkCodePhone(AuthRequestDTO.CheckCodePhoneDTO request) {

        PhoneAuth phoneAuth = phoneAuthRepository.findById(request.getPhoneAuthId())
                .orElseThrow(() -> new AuthHandler(ErrorStatus._DATA_NOT_FOUND));

        boolean codeCheck = request.getPhoneAuthCode().equals(phoneAuth.getCode());
        if (!codeCheck) throw new AuthHandler(ErrorStatus.INVALID_CODE_PHONE);

        boolean timeCheck = Duration.between(phoneAuth.getCreatedAt(), LocalDateTime.now()).getSeconds() < CodeSMS.VALID_TIME;
        if (!timeCheck) throw new AuthHandler(ErrorStatus.TIMEOUT_CODE_PHONE);

        phoneAuth.setIsChecked(true);
        return true;
    }

    @Override
    @Transactional
    public Member findPassword(AuthRequestDTO.FindPasswordDTO request) {
        EmailAuth emailAuth = emailAuthRepository.findById(request.getEmailAuthId())
                .orElseThrow(() -> new AuthHandler(ErrorStatus._DATA_NOT_FOUND));

        if(!emailAuthRepository.existsByIdAndPurposeAndIsChecked(request.getEmailAuthId(), Purpose.of(3), "T"))
            throw new AuthHandler(ErrorStatus.PHONE_NOT_CHECKED);

        return memberRepository.findByEmailAndStatus(emailAuth.getEmail(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    public Member login(AuthRequestDTO.LoginDTO request) {
        Member member = memberRepository.findByEmailAndLoginTypeAndStatus(request.getEmail(), LoginType.GENERAL, Status.ACTIVE)
                .orElseThrow(() -> new AuthHandler(ErrorStatus.MEMBER_NOT_FOUND));

        String inputPw =  member.getPwSalt() + request.getPassword();
        if(!passwordEncoder.matches(inputPw, member.getPwHash())) {
            throw new AuthHandler(ErrorStatus.LOGIN_FAILED_PASSWORD_INCORRECT);
        }
        return member;
    }

    @Override
    public AuthResponseDTO.LogoutResultDTO logout(String accessToken, String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || email.isEmpty()) {
            throw new AuthHandler(ErrorStatus.INVALID_JWT_TOKEN);
        }

        tokenManager.deleteRefreshToken(email);
        tokenManager.addBlackListAccessToken(accessToken, email);

        return AuthConverter.toLogoutResultDTO(email, accessToken);
    }

    @Override
    @Transactional
    public Member findEmail(AuthRequestDTO.FindEmailDTO request) {
        PhoneAuth phoneAuth = phoneAuthRepository.findById(request.getPhoneAuthId())
                .orElseThrow(() -> new AuthHandler(ErrorStatus._DATA_NOT_FOUND));

        if(!phoneAuthRepository.existsByIdAndPurposeAndIsChecked(request.getPhoneAuthId(), Purpose.of(2), "T"))
            throw new AuthHandler(ErrorStatus.PHONE_NOT_CHECKED);

        return memberRepository.findByPhoneAndStatus(phoneAuth.getPhone(), Status.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public Member getMember() {
        return memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional
    public Member resetPassword(AuthRequestDTO.ResetPasswordDTO request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (!request.getPassword().equals(request.getRePassword()))
            throw new AuthHandler(ErrorStatus.PASSWORD_NOT_CORRECT);

        passwordUtil.setMemberPassword(member, request.getRePassword());

        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public Member socialLogin(AuthRequestDTO.SocialLoginDTO request, int socialType) {
        // TODO: 전화번호가 변경되었을 때 어떻게 처리할지
        if (memberRepository.existsByEmailAndLoginTypeAndStatus(request.getEmail(), LoginType.of(socialType), Status.ACTIVE))
            return memberRepository.findByEmailAndLoginTypeAndStatus(request.getEmail(), LoginType.of(socialType), Status.ACTIVE)
                    .orElseThrow(() -> new AuthHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (memberRepository.existsByEmailAndStatus(request.getEmail(), Status.ACTIVE))
            throw new MemberHandler(ErrorStatus.MEMBER_EMAIL_DUPLICATE);
        if (memberRepository.existsByPhoneAndStatus(request.getPhone(), Status.ACTIVE))
            throw new MemberHandler(ErrorStatus.MEMBER_PHONE_DUPLICATE);
        Member newMember = AuthConverter.toSocialMember(request, socialType);
        passwordUtil.setSocialMemberPassword(newMember);

        if (memberRepository.existsByNicknameAndStatus(request.getNickname(), Status.ACTIVE))
            throw new AuthHandler(ErrorStatus.MEMBER_NICKNAME_DUPLICATE);

        if (request.getRole().equals(1)) enterBossInfo(request, newMember);
        else if (request.getRole().equals(2)) enterTeacherInfo(request, newMember);

        return memberRepository.save(newMember);
    }

    @Override
    public void enterBossInfo(AuthRequestDTO.JoinCommonDTO request, Member member) {
        member.setProfile(request.getNickname(), request.getProfileImg());
    }

    @Override
    @Transactional
    public void enterTeacherInfo(AuthRequestDTO.JoinCommonDTO request, Member member) {
        // TODO : 사업자 인증 여부 확인 로직 추가

        if (request.getBusinessNum() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_BUSINESS_NUM_EMPTY);
        if (request.getRepresentative() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_REPRESENTATIVE_EMPTY);
        if (request.getOpenDate() == null || request.getOpenDate().toString().isEmpty())
            throw new AuthHandler(ErrorStatus.MEMBER_OPEN_DATE_EMPTY);
        if (request.getBank() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_BANK_EMPTY);
        if (request.getAccountNum() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_ACCOUNT_NUM_EMPTY);
        if (request.getAccountHolder() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_ACCOUNT_HOLDER_EMPTY);
        if (request.getField() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_FIELD_EMPTY);
        if (request.getCareer() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_CAREER_EMPTY);
        if (request.getIntroduction() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_INTRODUCTION_EMPTY);
        if (request.getKeywords().length() < 2)
            throw new AuthHandler(ErrorStatus.MEMBER_KEYWORDS_EMPTY);

        member.setProfile(request.getNickname(), request.getProfileImg());
        TeacherInfo newTeacher = AuthConverter.toTeacher(request);
        teacherInfoRepository.save(newTeacher);
    }
}