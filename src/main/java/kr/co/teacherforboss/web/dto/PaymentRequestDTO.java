package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.co.teacherforboss.validation.annotation.CheckSurvey;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import lombok.extern.jackson.Jacksonized;

public class PaymentRequestDTO {

    @Getter
    @Builder
    public static class EditTeacherAccountDTO {

        @NotBlank(message = "은행명을 입력해주세요.")
        String bank;

        @NotBlank(message = "예금주를 입력해주세요.")
        String accountHolder;

        @NotBlank(message = "계좌번호를 입력해주세요.")
        String accountNumber;
    }

    @Getter
    @Jacksonized
    @Builder
    public static class ExchangeTeacherPointDTO {
        @NotNull
        int points;
    }
}
