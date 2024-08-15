package kr.co.teacherforboss.service.memberService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.converter.MemberConverter;
import kr.co.teacherforboss.domain.Answer;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.AnswerRepository;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberQueryServiceImpl implements MemberQueryService{

    private final AuthCommandService authCommandService;
    private final MemberRepository memberRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final AnswerRepository answerRepository;
  
    @Override
    @Transactional
    public MemberResponseDTO.GetMemberProfileDTO getMemberProfile(){
        Member member = authCommandService.getMember();
        memberRepository.findByIdAndStatus(member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        TeacherInfo teacherInfo = teacherInfoRepository.findByMemberIdAndStatus(member.getId(), Status.ACTIVE)
                .orElse(null);
        Integer answerCount = null;
        if (teacherInfo != null)
            answerCount = answerRepository.countAllByMemberIdAndSelectedAndStatus(member.getId(), BooleanType.T, Status.ACTIVE);
        return MemberConverter.toGetMemberProfileDTO(member, teacherInfo, answerCount);
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
    
    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO.GetRecentAnswersDTO getRecentAnswers() {
        Member member = authCommandService.getMember();
        if (member.getRole() != Role.TEACHER) {
            throw new MemberHandler(ErrorStatus.TEACHER_INFO_NOT_FOUND);
        }

        List<Answer> answers = answerRepository.findAllByMemberIdAndStatus(member.getId());
        return MemberConverter.toGetRecentAnswersDTO(answers);
    }
}
