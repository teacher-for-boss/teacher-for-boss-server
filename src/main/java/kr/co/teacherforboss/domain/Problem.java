package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.domain.common.BaseEntity;
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
public class Problem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examId")
    private Exam exam;

    @NotNull
    @Column(length = 50)
    private String name;

    @NotNull
    @Column
    private Long answer;    // ProblemChoice.id

    @NotNull
    @Column(length = 100)
    private String commentary;

    @NotNull
    @Column
    private Integer points;

    @NotNull
    @Column
    private Integer sequence;

    @OneToMany(mappedBy = "problem")
    private List<ProblemChoice> problemChoiceList;

}
