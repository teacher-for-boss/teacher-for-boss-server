package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.co.teacherforboss.validation.annotation.CheckSurvey;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PaymentRequestDTO {

    @Getter
    @Builder
    public static class EditTeacherAccountDTO {

        String bank;
        String accountHolder;
        String accountNumber;
    }
}
