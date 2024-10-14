package com.ticketing.performance.common.exception;

import com.ticketing.performance.common.response.ErrorCode;

public class S3Exception extends BusinessException {

    public S3Exception(ErrorCode errorCode) {
        super(errorCode);
    }
}
