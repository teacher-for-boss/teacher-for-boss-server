package kr.co.teacherforboss.converter;


import kr.co.teacherforboss.domain.Exchange;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.enums.BooleanType;
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

    public static Exchange toExchange(int points) {
        return Exchange.builder()
                .points(points)
                .isComplete(BooleanType.F)
                .build();
    }

    public static PaymentResponseDTO.ExchangeTeacherPointDTO toExchangeTeacherPoint(Exchange exchange) {
        return PaymentResponseDTO.ExchangeTeacherPointDTO.builder()
                .exchangeId(exchange.getId())
                .createdAt(exchange.getCreatedAt())
                .build();
    }

    public static PaymentResponseDTO.CompleteExchangeProcessDTO toCompleteExchangeProcess(Exchange exchange) {
        return PaymentResponseDTO.CompleteExchangeProcessDTO.builder()
                .isComplete(exchange.getIsComplete().isIdentifier())
                .updatedAt(exchange.getUpdatedAt())
                .build();
    }
}
