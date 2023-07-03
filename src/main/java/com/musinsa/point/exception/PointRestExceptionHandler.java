package com.musinsa.point.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class PointRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({BadRequestException.class})
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException e, WebRequest request) {
        var apiError = new ApiError(HttpStatus.BAD_REQUEST, e.getMessage(), e.getErrorCode());
        return handleExceptionInternal(e, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
