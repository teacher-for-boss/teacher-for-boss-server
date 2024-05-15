package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    BOSS(1),
    TEACHER(2),
    ADMIN(3);

    private final int identifier;

    public static Role of(int identifier) {
        if (identifier == 2) return TEACHER;
        if (identifier == 3) return ADMIN;
        return BOSS;
    }
}