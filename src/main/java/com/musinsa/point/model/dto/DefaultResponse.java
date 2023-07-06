package com.musinsa.point.model.dto;

import lombok.Getter;

@Getter
public class DefaultResponse {

    private final ResponseStatus status;

    public static DefaultResponse success() {
        return new DefaultResponse(ResponseStatus.SUCCESS);
    }

    public DefaultResponse(ResponseStatus status) {
        this.status = status;
    }

    public enum ResponseStatus {
        SUCCESS,
        FAIL
    }
}
