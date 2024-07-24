package kr.co.teacherforboss.apiPayload.exception.handler;

import kr.co.teacherforboss.apiPayload.code.BaseErrorCode;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;

public class HomeHandler extends GeneralException {

    public HomeHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
