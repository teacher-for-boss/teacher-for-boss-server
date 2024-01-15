package kr.co.teacherforboss.domain;

import jakarta.persistence.*;
import kr.co.teacherforboss.domain.enums.Purpose;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailAuth {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private Purpose purpose;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, columnDefinition = "VARCHAR(1)")
    private String isChecked;
}
