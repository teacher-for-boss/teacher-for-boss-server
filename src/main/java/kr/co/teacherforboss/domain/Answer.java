package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import kr.co.teacherforboss.domain.converter.StringConverter;
import kr.co.teacherforboss.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(indexes = @Index(name = "answer_selected_at_idx", columnList = "selected_at"))
public class Answer extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionId")
    private Question question;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "memberId")
    private Member member;

    @NotNull
    @Column(length = 5000)
    private String content;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime selectedAt;

    @NotNull
    @Column
    @ColumnDefault("0")
    private Integer likeCount;

    @NotNull
    @Column
    @ColumnDefault("0")
    private Integer dislikeCount;

    @Column(length = 36)
    private String imageUuid;

    @Column
    @Convert(converter = StringConverter.class)
    private List<String> imageIndex;

    public Answer editAnswer(String content, List<String> imageIndex, String imageUuid) {
        this.content = content;
        this.imageIndex = imageIndex;
        this.imageUuid = imageUuid; // TODO: imageUuid 업데이트 안하도록 수정
        return this;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

    public void increaseDislikeCount() {
        this.dislikeCount++;
    }

    public void decreaseDislikeCount() {
        this.dislikeCount--;
    }

    public void select() {
        this.selectedAt = LocalDateTime.now();;
    }
}
