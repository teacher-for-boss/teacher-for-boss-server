package kr.co.teacherforboss.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentRequestDTO {

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
