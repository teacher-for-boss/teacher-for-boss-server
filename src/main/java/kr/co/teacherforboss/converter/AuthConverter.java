package kr.co.teacherforboss.converter;

import kr.co.teacherforboss.domain.BusinessAuth;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.EmailAuth;
import kr.co.teacherforboss.domain.PhoneAuth;
import kr.co.teacherforboss.domain.TeacherSelectInfo;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.Gender;
import kr.co.teacherforboss.domain.enums.Level;
import kr.co.teacherforboss.domain.enums.LoginType;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Purpose;
import kr.co.teacherforboss.domain.AgreementTerm;
import kr.co.teacherforboss.util.AES256Util;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.web.dto.AuthResponseDTO;

import java.time.LocalDateTime;

public class AuthConverter {

    public static AuthResponseDTO.JoinResultDTO toJoinResultDTO(Member member){
        return AuthResponseDTO.JoinResultDTO.builder()
                .memberId(member.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Member toMember(AuthRequestDTO.JoinDTO request){
        Gender gender = Gender.of(request.getGender());

        return Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .loginType(LoginType.GENERAL)
                .role(Role.TEACHER_RV)
                .gender(gender)
                .birthDate(request.getBirthDate())
                .phone(request.getPhone())
                .build();
    }

    public static TeacherInfo toTeacher(AuthRequestDTO.JoinCommonDTO request, Member member){
        String keywords = String.join(";", request.getKeywords());
        return TeacherInfo.builder()
                .member(member)
//                .businessNumber(request.getBusinessNumber())
                .representative(request.getRepresentative())
                .openDate(request.getOpenDate())
                .field(request.getField())
                .career(request.getCareer())
                .introduction(request.getIntroduction())
                .keywords(keywords)
                .level(Level.LEVEL1)
                .bank(request.getBank())
                .accountNumber(AES256Util.encrypt(request.getAccountNumber()))
                .accountHolder(request.getAccountHolder())
                .emailOpen(BooleanType.F)
                .phoneOpen(BooleanType.F)
                .build();
    }

    public static AuthResponseDTO.SendCodeMailResultDTO toSendCodeMailResultDTO(EmailAuth emailAuth) {
        return AuthResponseDTO.SendCodeMailResultDTO.builder()
                .emailAuthId(emailAuth.getId())
                .createdAt(emailAuth.getCreatedAt())
                .build();
    }

    public static EmailAuth toEmailAuth(AuthRequestDTO.SendCodeMailDTO request) {
        return EmailAuth.builder()
                .email(request.getEmail())
                .purpose(Purpose.of(request.getPurpose()))
                .isChecked(BooleanType.F)
                .build();
    }

    public static BusinessAuth toBusinessAuth(AuthRequestDTO.CheckBusinessNumberDTO request) {
        return BusinessAuth.builder()
                .businessNumber(request.getBusinessNumber())
                .build();
    }

    public static AuthResponseDTO.SendCodePhoneResultDTO toSendCodePhoneResultDTO(PhoneAuth phoneAuth) {
        return AuthResponseDTO.SendCodePhoneResultDTO.builder()
                .phoneAuthId(phoneAuth.getId())
                .createdAt(phoneAuth.getCreatedAt())
                .build();
    }

    public static PhoneAuth toPhoneAuth(AuthRequestDTO.SendCodePhoneDTO request) {
        return PhoneAuth.builder()
                .phone(request.getPhone())
                .purpose(Purpose.of(request.getPurpose()))
                .isChecked(BooleanType.F)
                .build();
    }

    public static AuthResponseDTO.CheckResultDTO toCheckResultDTO(boolean isChecked) {
        return AuthResponseDTO.CheckResultDTO.builder()
                .isChecked(isChecked)
                .build();
    }

    public static AuthResponseDTO.FindPasswordResultDTO toFindPasswordResultDTO(Member member) {
        return AuthResponseDTO.FindPasswordResultDTO.builder()
                .memberId(member.getId())
                .build();
    }

    public static AuthResponseDTO.LogoutResultDTO toLogoutResultDTO(String email, String accessToken) {
        return AuthResponseDTO.LogoutResultDTO.builder()
                .email(email)
                .accessToken(accessToken)
                .logoutAt(LocalDateTime.now())
                .build();
    }

    public static AuthResponseDTO.WithdrawDTO toWithdrawResultDTO(Member member) {
        return AuthResponseDTO.WithdrawDTO.builder()
                .memberId(member.getId())
                .inactiveDate(member.getInactiveDate())
                .build();
    }

    public static AuthResponseDTO.ResetPasswordResultDTO toResetPasswordResultDTO(Member member) {
        return AuthResponseDTO.ResetPasswordResultDTO.builder()
                .memberId(member.getId())
                .isChanged(true)
                .build();
    }
  
    public static AuthResponseDTO.FindEmailResultDTO toFindEmailResultDTO(Member member) {
        return AuthResponseDTO.FindEmailResultDTO.builder()
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .build();
    }
  
    public static Member toSocialMember(AuthRequestDTO.SocialLoginDTO request, int socialType){
        Gender gender = Gender.of(request.getGender());

        return Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .role(Role.TEACHER_RV)
                .gender(gender)
                .loginType(LoginType.of(socialType))
                .birthDate(request.getBirthDate())
                .phone(request.getPhone())
                .profileImg(request.getProfileImg())
                .build();
    }
  
    public static AgreementTerm toAgreementTerm(AuthRequestDTO.JoinDTO request, Member member) {
        return AgreementTerm.builder()
                .member(member)
                .agreementSms(BooleanType.valueOf(request.getAgreementSms()))
                .agreementEmail(BooleanType.valueOf(request.getAgreementEmail()))
                .agreementLocation(BooleanType.valueOf(request.getAgreementLocation()))
                .build();
    }

    public static AuthResponseDTO.CheckNicknameResultDTO toCheckNicknameDTO(Boolean nicknameCheck) {
        return AuthResponseDTO.CheckNicknameResultDTO.builder()
                .nicknameCheck(nicknameCheck)
                .build();
    }

    public static AuthResponseDTO.CheckBusinessNumberResultDTO toCheckBusinessNumberResultDTO(BusinessAuth businessAuth) {
        return AuthResponseDTO.CheckBusinessNumberResultDTO.builder()
                .businessAuthId(businessAuth.getId())
                .isChecked(true)
                .build();
    }

    public static AuthResponseDTO.RecoverDTO toRecoverDTO(Member member) {
        return AuthResponseDTO.RecoverDTO.builder()
                .memberId(member.getId())
                .activeDate(LocalDateTime.now())
                .build();
    }

    public static AuthResponseDTO.CompleteTeacherSignupDTO toCompleteTeacherSignupDTO(Member member) {
        return AuthResponseDTO.CompleteTeacherSignupDTO.builder()
                .memberId(member.getId())
                .role(member.getRole())
                .build();
    }
}