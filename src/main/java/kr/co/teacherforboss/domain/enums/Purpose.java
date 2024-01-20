package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Purpose {

    NONE(0),
    SIGNUP(1),
    FINDEMAIL(2),
    FINDPW(3);

    private final int identifier;

    public static Purpose of(int identifier) {
        if (identifier == 1) return SIGNUP;
        if (identifier == 2) return FINDEMAIL;
        if (identifier == 3) return FINDPW;
        return NONE;
    }
}
