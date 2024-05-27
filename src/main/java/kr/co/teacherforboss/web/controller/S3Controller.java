package kr.co.teacherforboss.web.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.co.teacherforboss.apiPayload.ApiResponse;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Post;
import kr.co.teacherforboss.service.boardService.BoardCommandService;
import kr.co.teacherforboss.service.boardService.BoardQueryService;
import kr.co.teacherforboss.service.s3Service.S3QueryService;
import kr.co.teacherforboss.web.dto.BoardRequestDTO;
import kr.co.teacherforboss.web.dto.BoardResponseDTO;
import kr.co.teacherforboss.web.dto.S3RequestDTO;
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
    public ApiResponse<S3ResponseDTO.GetPresignedUrlDTO> getPresignedUrl(@RequestParam String type, @RequestParam Long id, @RequestParam Integer imageCount) {

        return ApiResponse.onSuccess(s3QueryService.getPresignedUrl(S3RequestDTO.GetPresignedUrlDTO.builder()
                        .type(type)
                        .id(id)
                        .imageCount(imageCount)
                .build()));
    }
}
