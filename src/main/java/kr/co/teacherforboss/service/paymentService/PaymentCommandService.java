package kr.co.teacherforboss.service.paymentService;

import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.web.dto.PaymentRequestDTO;

public interface PaymentCommandService {
    TeacherInfo editTeacherAccount(PaymentRequestDTO.EditTeacherAccountDTO request);
}
