package kr.co.teacherforboss.apiPayload.exception.handler;

import kr.co.teacherforboss.apiPayload.code.BaseErrorCode;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;

public class PaymentHandler extends GeneralException {

    public PaymentHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
