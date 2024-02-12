package kr.co.teacherforboss.apiPayload.exception.handler;

import kr.co.teacherforboss.apiPayload.code.BaseErrorCode;
import kr.co.teacherforboss.apiPayload.exception.GeneralException;

public class ExamHandler extends GeneralException {
    public ExamHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
