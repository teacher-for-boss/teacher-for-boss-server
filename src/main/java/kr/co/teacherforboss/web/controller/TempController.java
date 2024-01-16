package kr.co.teacherforboss.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Temp Controller", description = "테스트 API 입니다.")
@RestController
@RequestMapping("/api/v1/temp")
@RequiredArgsConstructor
public class TempController {

    @GetMapping("/test")
    public ApiResponse<String> testAPI(){

        return ApiResponse.onSuccess("success");
    }
}
