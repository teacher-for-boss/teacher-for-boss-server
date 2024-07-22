package kr.co.teacherforboss.service.paymentService;

import kr.co.teacherforboss.domain.Exchange;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.web.dto.PaymentRequestDTO;

public interface PaymentCommandService {
    TeacherInfo editTeacherAccount(PaymentRequestDTO.EditTeacherAccountDTO request);
    Exchange exchangeTeacherPoint(PaymentRequestDTO.ExchangeTeacherPointDTO request);
    Exchange completeExchangeProcess(Long exchangeId);
}
