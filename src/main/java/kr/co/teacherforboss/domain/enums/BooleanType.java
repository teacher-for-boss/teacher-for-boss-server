package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BooleanType {
    T(true),
    F(false);

    private final Boolean identifier;

    public static Boolean of(BooleanType type) {
        return type.equals(T);
    }
}