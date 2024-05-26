package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class BoardRequestDTO {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SavePostDTO{

        @NotNull(message = "제목은 필수 입력값입니다.")
        @Size(max = 30, message = "제목은 최대 30자 입력 가능합니다.")
        String title;

        @NotNull(message = "게시물 내용은 필수 입력값입니다.")
        @Size(max = 1000, message = "게시물 내용은 최대 1000자 입력 가능합니다.")
        String content;

        @Size(max = 3, message = "사진은 최대 3장까지 등록 가능합니다.")
        List<String> imageUrlList;

        @Size(max = 5, message = "해시태그는 최대 5개까지 등록 가능합니다.")
        List<String> hashtagList;
    }
}
