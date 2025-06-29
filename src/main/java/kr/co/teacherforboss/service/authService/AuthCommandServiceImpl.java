package kr.co.teacherforboss.service.authService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.config.jwt.TokenManager;
import kr.co.teacherforboss.converter.AuthConverter;
import kr.co.teacherforboss.domain.BusinessAuth;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.TeacherSelectInfo;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.LoginType;
import kr.co.teacherforboss.domain.AgreementTerm;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.vo.mailVO.TeacherAuditMail;
import kr.co.teacherforboss.repository.AgreementTermRepository;
import kr.co.teacherforboss.repository.BusinessAuthRepository;
import kr.co.teacherforboss.repository.TeacherSelectInfoRepository;
import kr.co.teacherforboss.util.BusinessUtil;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    @Value("${spring.mail.username}")
    private String ADMIN_MAIL;
    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PhoneAuthRepository phoneAuthRepository;
    private final AgreementTermRepository agreementTermRepository;
    private final BusinessAuthRepository businessAuthRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final TeacherSelectInfoRepository teacherSelectInfoRepository;
    private final MailCommandService mailCommandService;
    private final PasswordEncoder passwordEncoder;
    private final TokenManager tokenManager;
    private final SmsUtil smsUtil;
    private final PasswordUtil passwordUtil;
    private final BusinessUtil businessUtil;

    // 회원 가입
    @Override
    @Transactional
    public Member joinMember(AuthRequestDTO.JoinDTO request){
        validateRequiredFields(request);
        if (memberRepository.existsByEmailAndStatus(request.getEmail(), Status.ACTIVE))
            throw new AuthHandler(ErrorStatus.MEMBER_EMAIL_DUPLICATED);
        if (!emailAuthRepository.existsByIdAndEmailAndPurposeAndIsChecked(request.getEmailAuthId(), request.getEmail(),
                Purpose.of(1), BooleanType.T))
            throw new AuthHandler(ErrorStatus.MAIL_NOT_CHECKED);
        if (memberRepository.existsByPhoneAndStatus(request.getPhone(), Status.ACTIVE))
            throw new AuthHandler(ErrorStatus.MEMBER_PHONE_DUPLICATED);
        if (!request.getPassword().equals(request.getRePassword()))
            throw new AuthHandler(ErrorStatus.PASSWORD_NOT_CORRECT);
        if (!phoneAuthRepository.existsByIdAndPhoneAndPurposeAndIsChecked(request.getPhoneAuthId(), request.getPhone(),
                Purpose.of(1), BooleanType.T))
            throw new AuthHandler(ErrorStatus.PHONE_NOT_CHECKED);
        if (!(request.getAgreementUsage().equals("T") && request.getAgreementInfo().equals("T") && request.getAgreementAge().equals("T")))
            throw new AuthHandler(ErrorStatus.INVALID_AGREEMENT_TERM);
        if (memberRepository.existsByNicknameAndStatus(request.getNickname(), Status.ACTIVE))
            throw new AuthHandler(ErrorStatus.MEMBER_NICKNAME_DUPLICATED);

        Member newMember = AuthConverter.toMember(request);
        List<String> passwordList = passwordUtil.generatePassword(request.getRePassword());
        newMember.setPassword(passwordList);

        AgreementTerm newAgreement = AuthConverter.toAgreementTerm(request, newMember);

        newMember.setProfile(request.getNickname(), request.getProfileImg());

        if (Role.of(request.getRole()).equals(Role.TEACHER)) {
            TeacherInfo teacherInfo = saveTeacherInfo(request, newMember);
            saveTeacherSelectInfo(newMember);
            TeacherAuditMail teacherAuditMail = new TeacherAuditMail(newMember, teacherInfo);
            mailCommandService.sendMail(ADMIN_MAIL, teacherAuditMail);
        }
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

        if(!emailAuthRepository.existsByIdAndPurposeAndIsChecked(request.getEmailAuthId(), Purpose.FIND_PW, BooleanType.T))
            throw new AuthHandler(ErrorStatus.PHONE_NOT_CHECKED);

        return memberRepository.findByEmailAndStatus(emailAuth.getEmail(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional
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
    @Transactional
    public Member logout(String accessToken, String fcmToken) {
        Member member = getMember();

        tokenManager.deleteRefreshToken(member.getEmail());
        tokenManager.addBlackListAccessToken(accessToken, member.getEmail());

        return member;
    }

    @Override
    @Transactional
    public Member findEmail(AuthRequestDTO.FindEmailDTO request) {
        PhoneAuth phoneAuth = phoneAuthRepository.findById(request.getPhoneAuthId())
                .orElseThrow(() -> new AuthHandler(ErrorStatus._DATA_NOT_FOUND));

        if(!phoneAuthRepository.existsByIdAndPurposeAndIsChecked(request.getPhoneAuthId(), Purpose.FIND_EMAIL, BooleanType.T))
            throw new AuthHandler(ErrorStatus.PHONE_NOT_CHECKED);

        return memberRepository.findByPhoneAndStatus(phoneAuth.getPhone(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Member getMember() {
        return memberRepository.findByEmailAndStatus(SecurityUtil.getCurrentUserEmail(), Status.ACTIVE)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> getMembers(List<Long> memberIds) {
        return memberRepository.findAllById(memberIds);
    }

    @Override
    @Transactional
    public Member resetPassword(AuthRequestDTO.ResetPasswordDTO request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (!request.getPassword().equals(request.getRePassword()))
            throw new AuthHandler(ErrorStatus.PASSWORD_NOT_CORRECT);

        List<String> passwordList = passwordUtil.generatePassword(request.getRePassword());
        member.setPassword(passwordList);

        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public Member socialLogin(AuthRequestDTO.SocialLoginDTO request, int socialType) {
        // TODO: 전화번호가 변경되었을 때 어떻게 처리할지
        Optional<Member> member = memberRepository.findByEmailAndLoginTypeAndStatus(request.getEmail(), LoginType.of(socialType), Status.ACTIVE);
        if (member.isPresent()) return member.get();

        validateRequiredFields(request);
        if (memberRepository.existsByEmailAndStatus(request.getEmail(), Status.ACTIVE))
            throw new MemberHandler(ErrorStatus.MEMBER_EMAIL_DUPLICATED);
        if (memberRepository.existsByPhoneAndStatus(request.getPhone(), Status.ACTIVE))
            throw new MemberHandler(ErrorStatus.MEMBER_PHONE_DUPLICATED);
        if (memberRepository.existsByNicknameAndStatus(request.getNickname(), Status.ACTIVE))
            throw new AuthHandler(ErrorStatus.MEMBER_NICKNAME_DUPLICATED);

        Member newMember = AuthConverter.toSocialMember(request, socialType);
        List<String> passwordList = passwordUtil.generateSocialMemberPassword();
        newMember.setPassword(passwordList);

        newMember.setProfile(request.getNickname(), request.getProfileImg());
        if (Role.of(request.getRole()).equals(Role.TEACHER)) {
            TeacherInfo teacherInfo = saveTeacherInfo(request, newMember);
            saveTeacherSelectInfo(newMember);
            TeacherAuditMail teacherAuditMail = new TeacherAuditMail(newMember, teacherInfo);
            mailCommandService.sendMail(ADMIN_MAIL, teacherAuditMail);
        }
        return memberRepository.save(newMember);
    }

    @Override
    @Transactional
    public BusinessAuth checkBusinessNumber(AuthRequestDTO.CheckBusinessNumberDTO request) {
        boolean isChecked = businessUtil.validateBusinessNumber(request.getBusinessNumber(), request.getOpenDate(),
                request.getRepresentative());

        if(!isChecked) {
            throw new AuthHandler(ErrorStatus.INVALID_BUSINESS_INFO);
        }

        BusinessAuth businessAuth = businessAuthRepository.findByBusinessNumber(request.getBusinessNumber());
        if (businessAuth == null) {
            businessAuth = AuthConverter.toBusinessAuth(request);
        }
        businessAuth.setUpdatedAt(LocalDateTime.now());

        return businessAuthRepository.save(businessAuth);
    }

    private TeacherInfo saveTeacherInfo(AuthRequestDTO.JoinCommonDTO request, Member member) {
//         if (!businessAuthRepository.existsByBusinessNumber(request.getBusinessNumber()))
//             throw new AuthHandler(ErrorStatus.BUSINESS_NUMBER_NOT_CHECKED);
//        if (request.getBusinessNumber() == null)
//            throw new AuthHandler(ErrorStatus.MEMBER_BUSINESS_NUMBER_EMPTY);
//        if (request.getRepresentative() == null)
//            throw new AuthHandler(ErrorStatus.MEMBER_REPRESENTATIVE_EMPTY);
//        if (request.getOpenDate().toString().isBlank())
//            throw new AuthHandler(ErrorStatus.MEMBER_OPEN_DATE_EMPTY);
        if (request.getBank() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_BANK_EMPTY);
        if (request.getAccountNumber() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_ACCOUNT_NUMBER_EMPTY);
        if (request.getAccountHolder() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_ACCOUNT_HOLDER_EMPTY);
        if (request.getField() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_FIELD_EMPTY);
        if (request.getCareer() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_CAREER_EMPTY);
        if (request.getIntroduction() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_INTRODUCTION_EMPTY);
        if (request.getKeywords().isEmpty())
            throw new AuthHandler(ErrorStatus.MEMBER_KEYWORDS_EMPTY);

        TeacherInfo newTeacher = AuthConverter.toTeacher(request, member);
        return teacherInfoRepository.save(newTeacher);
    }

    private void saveTeacherSelectInfo(Member member) {
        TeacherSelectInfo teacherSelectInfo = TeacherSelectInfo.builder()
                .member(member)
                .points(0)
                .selectCount(0)
                .build();
        teacherSelectInfoRepository.save(teacherSelectInfo);
    }

    private void validateRequiredFields (AuthRequestDTO.JoinCommonDTO request) {
        if (request.getRole() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_ROLE_EMPTY);
        if (!(Role.of(request.getRole()).equals(Role.BOSS) || Role.of(request.getRole()).equals(Role.TEACHER)
                || Role.of(request.getRole()).equals(Role.TEACHER_RV)))
            throw new AuthHandler(ErrorStatus.MEMBER_ROLE_INVALID);
        if (request.getName() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_NAME_EMPTY);
        if (request.getNickname() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_NICKNAME_EMPTY);
        if (request.getPhone() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_PHONE_EMPTY);
        if (request.getProfileImg() == null)
            throw new AuthHandler(ErrorStatus.MEMBER_PROFILE_IMG_EMPTY);
    }

    @Override
    @Transactional
    public Member withdraw() {
        Member member = getMember();

        if (member.getRole() == Role.TEACHER) {
            TeacherInfo teacherInfo = teacherInfoRepository.findByMemberIdAndStatus(member.getId(), Status.ACTIVE)
                            .orElseThrow(() -> new MemberHandler(ErrorStatus.TEACHER_INFO_NOT_FOUND));
            teacherInfo.softDelete();
        }

        member.softDelete();
        return member;
    }

    @Override
    @Transactional
    public Member recover(String email) {
        Member member = memberRepository.findByEmailAndStatus(email, Status.INACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (member.getRole() == Role.TEACHER) {
            TeacherInfo teacherInfo = teacherInfoRepository.findByMemberIdAndStatus(member.getId(), Status.INACTIVE)
                            .orElseThrow(() -> new MemberHandler(ErrorStatus.TEACHER_INFO_NOT_FOUND));
            teacherInfo.revertSoftDelete();
        }

        member.setInactiveDate(null);
        member.revertSoftDelete();
        return member;
    }

    @Override
    @Transactional
    public Member completeTeacherSignup(AuthRequestDTO.CompleteTeacherSignupDTO request) {
        Member admin = getMember();
        Member member = memberRepository.findByIdAndStatus(request.getMemberId(), Status.ACTIVE)
                .orElseThrow(() -> new AuthHandler(ErrorStatus.MEMBER_NOT_FOUND));
        if (member.getRole() != Role.TEACHER_RV)
            throw new AuthHandler(ErrorStatus.MEMBER_NOT_UNDER_TEACHER_REVIEW);
        return member.setRole(Role.TEACHER);
    }
}
