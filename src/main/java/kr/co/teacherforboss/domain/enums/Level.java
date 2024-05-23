package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Level {
    NONE(""),
    LEVEL1("Lv.1 행운의 별"),
    LEVEL2("Lv.2 열정의 별"),
    LEVEL3("Lv.3 신뢰의 별"),
    LEVEL4("Lv.4 지식의 별"),
    LEVEL5("Lv.5 성공의 별");

    private final String level;
}
