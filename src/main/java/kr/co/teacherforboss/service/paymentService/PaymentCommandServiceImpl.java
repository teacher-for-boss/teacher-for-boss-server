package kr.co.teacherforboss.service.paymentService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.apiPayload.exception.handler.PaymentHandler;
import kr.co.teacherforboss.converter.PaymentConverter;
import kr.co.teacherforboss.domain.Exchange;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.TeacherSelectInfo;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.domain.vo.mailVO.ExchangeMail;
import kr.co.teacherforboss.repository.ExchangeRepository;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
import kr.co.teacherforboss.repository.TeacherSelectInfoRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.service.mailService.MailCommandService;
import kr.co.teacherforboss.web.dto.PaymentRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final AuthCommandService authCommandService;
    private final MailCommandService mailCommandService;
    private final TeacherInfoRepository teacherInfoRepository;
    private final TeacherSelectInfoRepository teacherSelectInfoRepository;
    private final ExchangeRepository exchangeRepository;

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

    @Override
    @Transactional
    public Exchange exchangeTeacherPoint(PaymentRequestDTO.ExchangeTeacherPointDTO request){
        Member member = authCommandService.getMember();
        if (member.getRole() != Role.TEACHER) throw new MemberHandler(ErrorStatus.MEMBER_ROLE_NOT_TEACHER);
        TeacherInfo teacherInfo = teacherInfoRepository.findByMemberIdAndStatus(member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.TEACHER_INFO_NOT_FOUND));
        TeacherSelectInfo teacherSelectInfo = teacherSelectInfoRepository.findByMemberIdAndStatus(member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.TEACHER_SELECT_INFO_NOT_FOUND));

        if (teacherSelectInfo.getPoints() < 550) throw new PaymentHandler(ErrorStatus.TEACHER_POINT_LIMIT_UNDER);
        if (request.getPoints() > teacherSelectInfo.getPoints()) throw new PaymentHandler(ErrorStatus.TEACHER_POINT_LIMIT_OVER);
        if (request.getPoints() % 100 != 0) throw new PaymentHandler(ErrorStatus.TEACHER_POINT_INVALID_UNIT_DIVISION);

        ExchangeMail exchangeMail = new ExchangeMail(teacherSelectInfo.getPoints(), request.getPoints(), teacherInfo);
        mailCommandService.sendMail(member.getEmail(), exchangeMail);

        Exchange exchange = PaymentConverter.toExchange(request.getPoints());
        teacherSelectInfo.decreasePoints(request.getPoints());
        teacherSelectInfoRepository.save(teacherSelectInfo);
        return exchangeRepository.save(exchange);
    }
}
