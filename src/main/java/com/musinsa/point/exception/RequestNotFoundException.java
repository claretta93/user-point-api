package com.musinsa.point.exception;

import lombok.Getter;

@Getter
public class RequestNotFoundException extends BadRequestException {

    public RequestNotFoundException() {
        super(ErrorCode.REQUEST_NOT_FOUND);
    }
}
