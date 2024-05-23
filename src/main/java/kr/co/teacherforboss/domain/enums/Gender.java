package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE(1),
    FEMALE(2),
    NONE(3);

    private final int identifier;

    public static Gender of(int identifier) {
        if (identifier == 1) return MALE;
        if (identifier == 2) return FEMALE;
        return NONE;
    }
}