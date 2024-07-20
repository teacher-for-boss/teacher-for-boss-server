package kr.co.teacherforboss.web.controller;

import kr.co.teacherforboss.validation.annotation.CheckImageOrigin;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.service.s3Service.S3QueryService;
import kr.co.teacherforboss.web.dto.S3ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3QueryService s3QueryService;

    @GetMapping("/presigned-url")
    public ApiResponse<S3ResponseDTO.GetPresignedUrlDTO> getPresignedUrl(@RequestParam @CheckImageOrigin String origin,
                                                                         @RequestParam(required = false) String uuid,
                                                                         @RequestParam(defaultValue = "0") int lastIndex,
                                                                         @RequestParam(defaultValue = "1") int imageCount,
                                                                         @RequestParam String fileType) {
        return ApiResponse.onSuccess(s3QueryService.getPresignedUrl(origin, uuid, lastIndex, imageCount, fileType));
    }
}
