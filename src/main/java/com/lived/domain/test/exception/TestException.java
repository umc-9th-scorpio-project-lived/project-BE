package com.lived.domain.test.exception;

import com.lived.global.apiPayload.code.BaseErrorCode;
import com.lived.global.apiPayload.exception.GeneralException;

public class TestException extends GeneralException {
    public TestException(BaseErrorCode code) {
        super(code);
    }
}
