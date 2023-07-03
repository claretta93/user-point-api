package com.musinsa.point.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    REMAIN_POINT_NOT_ENOUGH("잔여 포인트가 부족합니다."),
    REQUEST_NOT_FOUND("요청한 이력을 찾을 수 없습니다."),
    REQUEST_DUPLICATED("이미 처리된 요청입니다.")
    ;

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
