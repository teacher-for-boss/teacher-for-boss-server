package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.domain.common.BaseEntity;
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
public class BusinessAuth extends BaseEntity {

    @NotNull
    @Column(length = 10)
    private String businessNum;

    @NotNull
    @Column(columnDefinition = "VARCHAR(1)")
    @ColumnDefault("'F'")
    private String isChecked;

    public void setIsChecked(boolean isChecked) {
        if(isChecked) this.isChecked = "T";
        else this.isChecked = "F";
    }
}
