package kr.co.teacherforboss.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetTeacherAccountDTO {
        String bank;
        String accountNumber;
        String accountHolder;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditTeacherAccountDTO {
        LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExchangeTeacherPointDTO {
        Long exchangeId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompleteExchangeProcessDTO {
        boolean isComplete;
        LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetExchangeHistoryDTO {
        boolean hasNext;
        List<ExchangeHistory> exchangeList;

        @Getter
        @Builder
        @AllArgsConstructor
        public static class ExchangeHistory {
            long exchangeId;
            String type;
            int points;
            LocalDateTime time;
        }
    }
}
