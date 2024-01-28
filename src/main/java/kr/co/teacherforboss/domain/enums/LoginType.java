package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {
    NONE(0),
    KAKAO(1),
    NAVER(2),
    GENERAL(3);

    private final int identifier;

    public static LoginType of(int identifier) {
        if (identifier == 1) return KAKAO;
        if (identifier == 2) return NAVER;
        if (identifier == 3) return GENERAL;
        return NONE;
    }
}
