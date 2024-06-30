package kr.co.teacherforboss.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageOrigin {

    PROFILE("profiles"),
    POST("posts"),
    QUESTION("questions"),
    ANSWER("answers"),
    COMMENT("comments");

    private final String value;

}
