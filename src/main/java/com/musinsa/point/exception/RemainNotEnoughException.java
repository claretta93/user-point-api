package com.musinsa.point.exception;

import lombok.Getter;

@Getter
public class RemainNotEnoughException extends BadRequestException {

    public RemainNotEnoughException() {
        super(ErrorCode.REMAIN_POINT_NOT_ENOUGH);
    }
}
