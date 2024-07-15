package kr.co.teacherforboss.service.memberService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.converter.MemberConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.MemberSurvey;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.MemberSurveyRepository;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.MemberRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService{

    private final MemberSurveyRepository memberSurveyRepository;
    private final AuthCommandService authCommandService;
    private final TeacherInfoRepository teacherInfoRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MemberSurvey saveSurvey(MemberRequestDTO.SurveyDTO request) {
        Member member = authCommandService.getMember();

        if (memberSurveyRepository.existsByMember(member))
            throw new MemberHandler(ErrorStatus.SURVEY_DUPLICATED);

        MemberSurvey memberSurvey = MemberConverter.toMemberSurvey(request, member);
        return memberSurveyRepository.save(memberSurvey);
    }

    @Override
    @Transactional
    public Member editBossProfile(MemberRequestDTO.EditBossProfileDTO request) {
        Member member = authCommandService.getMember();
        if (!member.getRole().equals(Role.BOSS)) throw new MemberHandler(ErrorStatus.MEMBER_ROLE_INVALID);
        member.setProfile(request.getNickname(), request.getProfileImg());
        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public Member editTeacherProfile(MemberRequestDTO.EditTeacherProfileDTO request) {
        Member member = authCommandService.getMember();
        if (!member.getRole().equals(Role.TEACHER)) throw new MemberHandler(ErrorStatus.MEMBER_ROLE_INVALID);
        TeacherInfo teacherInfo = teacherInfoRepository.findByMemberIdAndStatus(member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_TEACHER_INFO_NOT_FOUND));

        member.setProfile(request.getNickname(), request.getProfileImg());
        teacherInfo.editTeacherInfo(request.getField(), request.getCareer(), request.getIntroduction(), request.getKeywords(),
                request.getEmail(), BooleanType.of(request.isEmailOpen()), request.getPhone(), BooleanType.of(request.isPhoneOpen()));
        return memberRepository.save(member);
    }

}
