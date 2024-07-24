package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Level {
    NONE("", 0, 0),
    LEVEL1("Lv.1 행운의 별", 0, 30),
    LEVEL2("Lv.2 열정의 별", 31, 60),
    LEVEL3("Lv.3 신뢰의 별", 61, 90),
    LEVEL4("Lv.4 지식의 별", 91, 120),
    LEVEL5("Lv.5 성공의 별", 121, 0);

    private final String level;
    private final int first;
    private final int last;
}
