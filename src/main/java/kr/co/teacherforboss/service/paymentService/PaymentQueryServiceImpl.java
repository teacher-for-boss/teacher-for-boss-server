package kr.co.teacherforboss.service.paymentService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.converter.PaymentConverter;
import kr.co.teacherforboss.domain.Exchange;
import kr.co.teacherforboss.apiPayload.exception.handler.PaymentHandler;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.TeacherSelectInfo;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.ExchangeRepository;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
import kr.co.teacherforboss.repository.TeacherSelectInfoRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentQueryServiceImpl implements PaymentQueryService {

    private final AuthCommandService authCommandService;
    private final TeacherInfoRepository teacherInfoRepository;
    private final ExchangeRepository exchangeRepository;
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
    public PaymentResponseDTO.GetExchangeHistoryDTO getExchangeHistory(Long lastExchangeId, int size){
        Member member = authCommandService.getMember();
        PageRequest pageRequest = PageRequest.of(0, size);
        Slice<Exchange> exchanges = lastExchangeId == 0 ?
                exchangeRepository.findSliceByMemberIdAndStatusOrderByCreatedAtDesc(member.getId(), Status.ACTIVE, pageRequest) :
                exchangeRepository.findSliceByIdLessthanAndMemberIdOrderByCreatedAtDesc(lastExchangeId, member.getId(), pageRequest);
        return PaymentConverter.toGetExchangeHistory(exchanges);
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
