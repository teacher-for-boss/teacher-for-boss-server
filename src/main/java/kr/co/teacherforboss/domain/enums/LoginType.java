package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {
    GENERAL(1),
    KAKAO(2),
    NAVER(3);

    private final int identifier;

    public static LoginType of(int identifier) {
        if (identifier == 2) return KAKAO;
        if (identifier == 3) return NAVER;
        return GENERAL;
    }
}
