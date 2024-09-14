package kr.co.teacherforboss.service.paymentService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.apiPayload.exception.handler.PaymentHandler;
import kr.co.teacherforboss.converter.PaymentConverter;
import kr.co.teacherforboss.domain.Exchange;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.TeacherSelectInfo;
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

        TeacherInfo teacherInfo = teacherInfoRepository.findByMemberIdAndStatus(member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.TEACHER_INFO_NOT_FOUND));

        teacherInfo.editTeacherAccount(request.getBank(), request.getAccountNumber(), request.getAccountHolder());
        return teacherInfo;
    }

    @Override
    @Transactional
    public Exchange exchangeTeacherPoints(PaymentRequestDTO.ExchangeTeacherPointsDTO request){
        Member member = authCommandService.getMember();
        TeacherInfo teacherInfo = teacherInfoRepository.findByMemberIdAndStatus(member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.TEACHER_INFO_NOT_FOUND));
        TeacherSelectInfo teacherSelectInfo = teacherSelectInfoRepository.findByMemberIdAndStatus(member.getId(), Status.ACTIVE)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.TEACHER_SELECT_INFO_NOT_FOUND));

        if (request.getPoints() > teacherSelectInfo.getPoints()) throw new PaymentHandler(ErrorStatus.TEACHER_POINT_LIMIT_OVER);

        // TODO: 메일 보내는거 비동기로 처리
        ExchangeMail exchangeMail = new ExchangeMail(teacherSelectInfo.getPoints(), request.getPoints(), teacherInfo);
        mailCommandService.sendMail(member.getEmail(), exchangeMail);

        Exchange exchange = PaymentConverter.toExchange(member, request.getPoints());
        teacherSelectInfo.decreasePoints(request.getPoints());
        return exchangeRepository.save(exchange);
    }

    @Override
    @Transactional
    public Exchange completeExchangeProcess(Long exchangeId){
        Member member = authCommandService.getMember();
        Exchange exchange = exchangeRepository.findByIdAndStatus(exchangeId, Status.ACTIVE)
                .orElseThrow(() -> new PaymentHandler(ErrorStatus.EXCHANGE_NOT_FOUND));
        if (exchange.getIsComplete().isIdentifier()) throw new PaymentHandler(ErrorStatus.EXCHANGE_PROCESS_ALREADY_COMPLETE);
        exchange.complete();
        return exchange;
    }
}
