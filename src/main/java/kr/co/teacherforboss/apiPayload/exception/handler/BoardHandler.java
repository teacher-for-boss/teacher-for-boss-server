package kr.co.teacherforboss.apiPayload.exception.handler;

import kr.co.teacherforboss.apiPayload.code.BaseErrorCode;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;

public class BoardHandler extends GeneralException {

    public BoardHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
