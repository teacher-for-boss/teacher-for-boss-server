package kr.co.teacherforboss.service.paymentService;

import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.TeacherSelectInfo;


public interface PaymentQueryService {
    TeacherInfo getTeacherInfo();
    TeacherSelectInfo getTeacherPoints();
}
