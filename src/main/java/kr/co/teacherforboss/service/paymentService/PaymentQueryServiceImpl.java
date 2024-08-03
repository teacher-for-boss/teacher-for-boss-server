package kr.co.teacherforboss.service.paymentService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.apiPayload.exception.handler.PaymentHandler;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.TeacherSelectInfo;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
import kr.co.teacherforboss.repository.TeacherSelectInfoRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentQueryServiceImpl implements PaymentQueryService {

    private final AuthCommandService authCommandService;
    private final TeacherInfoRepository teacherInfoRepository;
    private final TeacherSelectInfoRepository teacherSelectInfoRepository;

    @Override
    @Transactional
    public TeacherInfo getTeacherInfo(){
        Member member = authCommandService.getMember();
        if (member.getRole() != Role.TEACHER) throw new MemberHandler(ErrorStatus.MEMBER_ROLE_NOT_TEACHER);
        return teacherInfoRepository.findByMemberIdAndStatus(member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.TEACHER_INFO_NOT_FOUND));
    }

    @Override
    @Transactional
    public TeacherSelectInfo getTeacherPoints(){
        Member member = authCommandService.getMember();
        if (member.getRole() != Role.TEACHER) throw new MemberHandler(ErrorStatus.MEMBER_ROLE_NOT_TEACHER);
        return teacherSelectInfoRepository.findByMemberIdAndStatus(member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new PaymentHandler(ErrorStatus.TEACHER_SELECT_INFO_NOT_FOUND));
    }
}
