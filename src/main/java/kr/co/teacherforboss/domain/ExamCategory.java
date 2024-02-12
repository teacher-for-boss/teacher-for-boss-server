package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.ExamType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExamCategory extends BaseEntity {

    @NotNull
    @Column(length = 10)
    private String categoryName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private ExamType examType;

}
