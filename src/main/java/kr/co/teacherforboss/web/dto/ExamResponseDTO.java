package kr.co.teacherforboss.web.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ExamResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TakeExamsDTO{
        Long memberExamId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetExamCategoriesDTO{
        List<ExamCategoryDTO> examCategoryList;
    }

    @Getter
    @AllArgsConstructor
    public static class ExamCategoryDTO{
        Long examCategoryId;
        String categoryName;
    }
}
