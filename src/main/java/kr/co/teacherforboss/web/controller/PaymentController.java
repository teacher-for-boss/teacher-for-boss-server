package kr.co.teacherforboss.web.controller;

import jakarta.validation.Valid;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.PaymentConverter;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.service.paymentService.PaymentCommandService;
import kr.co.teacherforboss.service.paymentService.PaymentQueryService;
import kr.co.teacherforboss.web.dto.PaymentRequestDTO;
import kr.co.teacherforboss.web.dto.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentQueryService paymentQueryService;
    private final PaymentCommandService paymentCommandService;

    @GetMapping("/accounts")
    public ApiResponse<PaymentResponseDTO.GetTeacherAccountDTO> getTeacherAccount() {
        TeacherInfo teacherInfo = paymentQueryService.getTeacherInfo();
        return ApiResponse.onSuccess(PaymentConverter.toGetTeacherAccountDTO(teacherInfo));
    }

    @PatchMapping("/accounts")
    public ApiResponse<PaymentResponseDTO.EditTeacherAccountDTO> editTeacherAccount(@RequestBody @Valid PaymentRequestDTO.EditTeacherAccountDTO request) {
        TeacherInfo teacherInfo = paymentCommandService.editTeacherAccount(request);
        return ApiResponse.onSuccess(PaymentConverter.toEditTeacherAccountDTO(teacherInfo));
    }

}
