package kr.co.teacherforboss.apiPayload.exception.handler;

import kr.co.teacherforboss.apiPayload.code.BaseErrorCode;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;

public class MailHandler extends GeneralException {

    public MailHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
