package kr.co.teacherforboss.service.paymentService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.PaymentRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final AuthCommandService authCommandService;
    private final TeacherInfoRepository teacherInfoRepository;

    @Override
    @Transactional
    public TeacherInfo editTeacherAccount(PaymentRequestDTO.EditTeacherAccountDTO request){
        Member member = authCommandService.getMember();
        if (member.getRole() != Role.TEACHER) throw new MemberHandler(ErrorStatus.MEMBER_ROLE_NOT_TEACHER);
        if (request.getBank() == null && request.getAccountNumber() == null && request.getAccountHolder() == null)
            throw new MemberHandler(ErrorStatus.MEMBER_ACCOUNT_INFO_EMPTY);

        TeacherInfo teacherInfo = teacherInfoRepository.findByMemberIdAndStatus(member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.TEACHER_INFO_NOT_FOUND));

        teacherInfo.editTeacherAccount(request.getBank(), request.getAccountNumber(), request.getAccountHolder());
        return teacherInfoRepository.save(teacherInfo);
    }
}
