package ru.pashabezborod.bi_test.exception;

import java.util.Map;

public class NoRequiredParameterException extends MBadRequest {

    private static final String MESSAGE = "No required parameter found";

    public NoRequiredParameterException(Map<ExceptionField, String> causes, Throwable cause) {
        super(MESSAGE, causes, cause);
    }
}
