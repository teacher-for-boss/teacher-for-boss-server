package kr.co.teacherforboss.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.co.teacherforboss.domain.common.BaseEntity;
import kr.co.teacherforboss.domain.enums.BooleanType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AnswerLike extends BaseEntity {
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answerId")
    private Answer answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Enumerated(EnumType.STRING)
    private BooleanType liked;

    public void toggleLiked() {
        if (this.liked == null) { // null -> T
            this.liked = BooleanType.T;
            this.answer.increaseLikeCount();
        }
        else if (this.liked.equals(BooleanType.F)) { // F -> T
            this.liked = BooleanType.T;
            this.answer.increaseLikeCount();
            this.answer.decreaseDislikeCount();
        }
        else { // T -> null
            this.liked = null;
            this.answer.decreaseLikeCount();
        }
    }

    public void toggleDisliked() {
        if (this.liked == null) { // null -> F
            this.liked = BooleanType.F;
            this.answer.increaseDislikeCount();

        }
        else if (this.liked.equals(BooleanType.T)) { // T -> F
            this.liked = BooleanType.F;
            this.answer.increaseDislikeCount();
            this.answer.decreaseLikeCount();
        }
        else { // F -> null
            this.liked = null;
            this.answer.decreaseDislikeCount();
        }
    }
}