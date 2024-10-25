package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import kr.co.teacherforboss.validation.annotation.CheckImageUuid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardRequestDTO {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SavePostDTO {

        @NotNull(message = "제목은 필수 입력값입니다.")
        @Size(max = 30, message = "제목은 최대 30자 입력 가능합니다.")
        String title;

        @NotNull(message = "게시물 내용은 필수 입력값입니다.")
        @Size(max = 1000, message = "게시물 내용은 최대 1000자 입력 가능합니다.")
        String content;

        @Size(max = 3, message = "사진은 최대 3장까지 등록 가능합니다.")
        @CheckImageUuid
        List<String> imageUrlList;

        @Size(max = 5, message = "해시태그는 최대 5개까지 등록 가능합니다.")
        List<String> hashtagList;
    }

    @Getter
    public static abstract class SaveQuestionDTO {

        @NotNull(message = "카테고리는 필수 입력값입니다.")
        Long categoryId;

        @NotNull(message = "제목은 필수 입력값입니다.")
        @Size(max = 30, message = "제목은 최대 30자 입력 가능합니다.")
        String title;

        @NotNull(message = "질문 내용은 필수 입력값입니다.")
        @Size(max = 1000, message = "질문 내용은 최대 1000자 입력 가능합니다.")
        String content;

        @Size(max = 5, message = "해시태그는 최대 5개까지 등록 가능합니다.")
        List<String> hashtagList;

        @Size(max = 3, message = "사진은 최대 3장까지 등록 가능합니다.")
        @CheckImageUuid
        List<String> imageUrlList;
    }

    @Getter
    @Builder
    public static class SaveMarketQuestionDTO extends SaveQuestionDTO {
        @NotNull(message = "작성자 유형은 필수 입력값입니다.")
        Integer bossType;
        @Size(max = 200)
        String businessType;
        @Size(max = 200)
        String location;
        @Size(max = 200)
        String customerType;
        @Size(max = 200)
        String storeInfo;
        @Size(max = 200)
        String budget;
    }

    @Getter
    @Builder
    public static class SaveTaxQuestionDTO extends SaveQuestionDTO {
        @NotNull(message = "세무 기장 여부는 필수 입력값입니다.")
        Integer taxFilingStatus;
        @Size(max = 200)
        String businessInfo;
        @Size(max = 200)
        String branchInfo;
        @Size(max = 200)
        String employeeManagement;
        @Size(max = 200)
        String purchaseEvidence;
        @Size(max = 200)
        String salesScale;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EditQuestionDTO{

        @NotNull(message = "카테고리는 필수 입력값입니다.")
        Long categoryId;

        @NotNull(message = "제목은 필수 입력값입니다.")
        @Size(max = 30, message = "제목은 최대 30자 입력 가능합니다.")
        String title;

        @NotNull(message = "질문 내용은 필수 입력값입니다.")
        @Size(max = 1000, message = "질문 내용은 최대 1000자 입력 가능합니다.")
        String content;

        @Size(max = 5, message = "해시태그는 최대 5개까지 등록 가능합니다.")
        List<String> hashtagList;

        @Size(max = 3, message = "사진은 최대 3장까지 등록 가능합니다.")
        @CheckImageUuid
        List<String> imageUrlList;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SaveAnswerDTO {

        @NotNull(message = "댓글 내용은 필수 입력값입니다.")
        @Size(max = 3000, message = "댓글 내용은 최대 3000자 입력 가능합니다.")
        String content;

        @Size(max = 3, message = "사진은 최대 3장까지 등록 가능합니다.")
        @CheckImageUuid
        List<String> imageUrlList;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EditAnswerDTO {

        @NotNull(message = "댓글 내용은 필수 입력값입니다.")
        @Size(max = 3000, message = "댓글 내용은 최대 3000자 입력 가능합니다.")
        String content;

        @Size(max = 3, message = "사진은 최대 3장까지 등록 가능합니다.")
        @CheckImageUuid
        List<String> imageUrlList;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SaveCommentDTO {

        Long parentId;

        @NotNull(message = "댓글 내용은 필수 입력값입니다.")
        @Size(max = 400, message = "댓글 내용은 최대 400자 입력 가능합니다.")
        String content;
    }
}
