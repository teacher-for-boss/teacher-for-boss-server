package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BooleanType {
    T(true),
    F(false);

    private final boolean identifier;

    public static BooleanType of(String booleanType) {
        return booleanType.equals("T") ? T : F;
    }
}