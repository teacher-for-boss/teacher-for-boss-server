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
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommentLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'F'")
    private BooleanType liked;


    public void toggleLiked() {
        if (this.liked == null) {
            this.liked = BooleanType.T;
            this.comment.increaseLikeCount();
        }
        else if (this.liked.equals(BooleanType.F)) {
            this.liked = BooleanType.T;
            this.comment.increaseLikeCount();
            this.comment.decreaseDislikeCount();
        }
        else {
            this.liked = null;
            this.comment.decreaseLikeCount();
        }
    }

    public void toggleDisliked() {
        if (this.liked == null) {
            this.liked = BooleanType.F;
            this.comment.increaseDislikeCount();

        }
        else if (this.liked.equals(BooleanType.T)) {
            this.liked = BooleanType.F;
            this.comment.increaseDislikeCount();
            this.comment.decreaseLikeCount();
        }
        else {
            this.liked = null;
            this.comment.decreaseDislikeCount();
        }
    }
}
