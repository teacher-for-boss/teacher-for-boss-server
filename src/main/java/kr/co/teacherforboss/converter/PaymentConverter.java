package kr.co.teacherforboss.converter;


import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.util.AES256Util;
import kr.co.teacherforboss.web.dto.PaymentResponseDTO;

public class PaymentConverter {
    public static PaymentResponseDTO.GetTeacherAccountDTO toGetTeacherAccountDTO(TeacherInfo teacherInfo) {
        return PaymentResponseDTO.GetTeacherAccountDTO.builder()
                .bank(teacherInfo.getBank())
                .account_number(AES256Util.decrypt(teacherInfo.getAccountNumber()))
                .account_holder(teacherInfo.getAccountHolder())
                .build();
    }
}
