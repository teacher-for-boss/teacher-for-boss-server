package kr.co.teacherforboss.web.controller;

import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.PaymentConverter;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.service.paymentService.PaymentQueryService;
import kr.co.teacherforboss.web.dto.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentQueryService paymentQueryService;

    @GetMapping("/accounts")
    public ApiResponse<PaymentResponseDTO.GetTeacherAccountDTO> getTeacherAccount() {
        TeacherInfo teacherInfo = paymentQueryService.getTeacherAccount();
        return ApiResponse.onSuccess(PaymentConverter.toGetTeacherAccountDTO(teacherInfo));
    }
}
