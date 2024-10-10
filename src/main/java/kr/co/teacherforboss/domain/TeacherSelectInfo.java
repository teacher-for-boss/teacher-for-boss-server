package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.domain.common.BaseEntity;
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
public class TeacherSelectInfo extends BaseEntity {

    public final static int POINT = 0;
    public final static int COUNT = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @NotNull
    @Column
    @Builder.Default
    private Integer points = 0;

    @NotNull
    @Column
    @Builder.Default
    private Integer selectCount = 0;

    public void increaseSelectCount() {
        this.selectCount++;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void decreasePoints(int points) {
        this.points -= points;
    }
}
