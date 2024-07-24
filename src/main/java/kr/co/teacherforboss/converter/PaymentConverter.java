package kr.co.teacherforboss.converter;


import kr.co.teacherforboss.domain.Exchange;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.BooleanType;
import kr.co.teacherforboss.domain.enums.ExchangeType;
import kr.co.teacherforboss.util.AES256Util;
import kr.co.teacherforboss.web.dto.PaymentResponseDTO;

public class PaymentConverter {
    public static PaymentResponseDTO.GetTeacherAccountDTO toGetTeacherAccountDTO(TeacherInfo teacherInfo) {
        return PaymentResponseDTO.GetTeacherAccountDTO.builder()
                .bank(teacherInfo.getBank())
                .accountNumber(AES256Util.decrypt(teacherInfo.getAccountNumber()))
                .accountHolder(teacherInfo.getAccountHolder())
                .build();
    }

    public static PaymentResponseDTO.EditTeacherAccountDTO toEditTeacherAccountDTO(TeacherInfo teacherInfo) {
        return PaymentResponseDTO.EditTeacherAccountDTO.builder()
                .updatedAt(teacherInfo.getUpdatedAt())
                .build();
    }

    public static Exchange toExchange(Member member, int points) {
        return Exchange.builder()
                .member(member)
                .exchangeType(ExchangeType.EX)
                .points(points)
                .isComplete(BooleanType.F)
                .build();
    }

    public static PaymentResponseDTO.ExchangeTeacherPointDTO toExchangeTeacherPoints(Exchange exchange) {
        return PaymentResponseDTO.ExchangeTeacherPointDTO.builder()
                .exchangeId(exchange.getId())
                .createdAt(exchange.getCreatedAt())
                .build();
    }
}
