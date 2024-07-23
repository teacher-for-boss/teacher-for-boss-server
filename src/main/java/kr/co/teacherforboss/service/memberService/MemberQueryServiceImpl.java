package kr.co.teacherforboss.service.memberService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.converter.MemberConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberQueryServiceImpl implements MemberQueryService{

    private final MemberRepository memberRepository;
    private final AuthCommandService authCommandService;
    private final TeacherInfoRepository teacherInfoRepository;

    @Override
    @Transactional
    public Member getMemberProfile(){
        Member member = authCommandService.getMember();
        return memberRepository.findByIdAndStatus(member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional
    public MemberResponseDTO.GetTeacherProfileDetailDTO getTeacherProfileDetail(Long memberId) {
        Member member = authCommandService.getMember();
        TeacherInfo teacherInfo = teacherInfoRepository.findByMemberIdAndStatus(memberId == null ? member.getId() : memberId, Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.TEACHER_INFO_NOT_FOUND));

        boolean isMine = member.equals(teacherInfo.getMember());

        return MemberConverter.toGetTeacherProfileDetailDTO(member, teacherInfo, isMine);
    }
}
