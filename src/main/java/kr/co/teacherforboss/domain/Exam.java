package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.ExamType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Exam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examCategoryId")
    private ExamCategory examCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examTagId")
    private ExamTag examTag;

    @NotNull
    @Column(length = 60)
    private String description;

    @NotNull
    @Column(length = 30)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private ExamType examType;

    @OneToMany(mappedBy = "exam")
    private List<Problem> problemList;

    public void setProblemList(List<Problem> problemList) {
        this.problemList = problemList;
    }
}
