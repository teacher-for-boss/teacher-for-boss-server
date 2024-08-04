package kr.co.teacherforboss.service.paymentService;

import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.web.dto.PaymentResponseDTO;
import kr.co.teacherforboss.domain.TeacherSelectInfo;


public interface PaymentQueryService {
    TeacherInfo getTeacherInfo();
    PaymentResponseDTO.GetExchangeHistoryDTO getExchangeHistory(Long lastExchangeId, int size);
    TeacherSelectInfo getTeacherPoints();
}
