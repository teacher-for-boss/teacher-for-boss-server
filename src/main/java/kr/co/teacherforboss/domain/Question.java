package kr.co.teacherforboss.domain;

import static java.time.LocalDateTime.now;

import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import kr.co.teacherforboss.domain.converter.QuestionExtraDataConverter;
import kr.co.teacherforboss.domain.vo.questionVO.QuestionExtraData;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

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
import kr.co.teacherforboss.domain.converter.StringConverter;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.BooleanType;
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
@Table(indexes = @Index(name = "question_created_at_idx", columnList = "created_at"))
public class Question extends BaseEntity {

    // TODO: yml로 숨기든 question에 point column을 추가하든 하기
    public final static int POINT = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "memberId")
    private Member member;

    @NotNull
    @Column(length = 30)
    String title;

    @NotNull
    @Column(length = 1000)
    String content;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = QuestionExtraDataConverter.class)
    private QuestionExtraData extraData;

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

    public Question editQuestion(Category category, String title, String content, List<String> imageIndex, String imageUuid) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.imageIndex = imageIndex;
        this.imageUuid = imageUuid; // TODO: imageUuid 업데이트 안하도록 수정
        return this;
    }

    public void selectAnswer(Answer answer) {
        this.solved = BooleanType.T;
        answer.select();
    }

    public Question increaseViewCount() {
        this.viewCount += 1;
        return this;
    }

    public boolean isSelectTermExpired() {
        return this.getCreatedAt().plusDays(7).isBefore(now());
    }

    public Question updateLikeCount(boolean isLike) {
        if (isLike) {
            this.likeCount += 1;
        } else {
            this.likeCount -= 1;
        }
        return this;
    }

    public Question updateBookmarkCount(boolean isBookmark) {
        if (isBookmark) {
            this.bookmarkCount += 1;
        } else {
            this.bookmarkCount -= 1;
        }
        return this;
    }

    public LocalDate getClosedDate() {
        return this.getCreatedAt().plusDays(7).toLocalDate();
    }
}

