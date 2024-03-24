package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExamQuarter {
    QUARTER1(1, 3),
    QUARTER2(4, 6),
    QUARTER3(7, 9),
    QUARTER4(10, 12);

    private final int first;
    private final int last;
}
