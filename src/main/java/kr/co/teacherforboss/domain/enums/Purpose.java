package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Purpose {

    NONE(0),
    SIGNUP(1),
    FIND_EMAIL(2),
    FIND_PW(3);

    private final int identifier;

    public static Purpose of(int identifier) {
        if (identifier == 1) return SIGNUP;
        if (identifier == 2) return FIND_EMAIL;
        if (identifier == 3) return FIND_PW;
        return NONE;
    }
}
