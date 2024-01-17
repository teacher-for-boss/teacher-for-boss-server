package kr.co.teacherforboss.apiPayload.exception.handler;

import kr.co.teacherforboss.apiPayload.code.BaseErrorCode;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;

public class AuthHandler extends GeneralException {

    public AuthHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
