package kr.co.teacherforboss.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.converter.StringConverter;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.BooleanType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Question extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @NotNull
    @Column(length = 30)
    String title;

    @NotNull
    @Column(length = 1000)
    String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'F'")
    private BooleanType solved;

    @NotNull
    @Column
    @ColumnDefault("0")
    private Integer likeCount;

    @NotNull
    @Column
    @ColumnDefault("0")
    private Integer viewCount;

    @NotNull
    @Column
    @ColumnDefault("0")
    private Integer bookmarkCount;

    @Column(length = 36)
    private String imageUuid;

    @Column
    @Convert(converter = StringConverter.class)
    private List<String> imageIndex;

    @OneToMany(mappedBy = "question")
    @SQLRestriction(value = "status = 'ACTIVE'")
    @Builder.Default
    private List<QuestionHashtag> hashtagList = new ArrayList<>();

    @OneToMany(mappedBy = "question")
    @SQLRestriction(value = "status = 'ACTIVE'")
    @Builder.Default
    private List<Answer> answerList = new ArrayList<>();

    public Question editQuestion(Category category, String title, String content, List<String> imageIndex) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.imageIndex = imageIndex;
        return this;
    }
}

