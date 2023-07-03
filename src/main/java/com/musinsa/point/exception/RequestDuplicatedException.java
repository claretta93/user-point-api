package com.musinsa.point.exception;

public class RequestDuplicatedException extends BadRequestException {

    public RequestDuplicatedException() { super(ErrorCode.REQUEST_DUPLICATED); }
}
