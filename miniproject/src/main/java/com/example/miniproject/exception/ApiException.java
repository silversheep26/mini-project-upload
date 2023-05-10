package com.example.miniproject.exception;

import com.example.miniproject.config.SentrySupport;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private ExceptionEnum error;
    private SentrySupport sentrySupport;

    public ApiException(ExceptionEnum e) {
        super(e.getMessage());
        this.error = e;
        sentrySupport.logSimpleMessage(e.getMessage());
    }
}
