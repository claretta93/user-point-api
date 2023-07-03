package com.musinsa.point.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiError {

    private final Integer status;
    private final String type;
    private final String message;
    private final ErrorCode errorCode;

    @Builder
    public ApiError(HttpStatus status, String message, ErrorCode errorCode) {
        this.status = status.value();
        this.type = status.getReasonPhrase();
        this.message = message;
        this.errorCode = errorCode;
    }

}
