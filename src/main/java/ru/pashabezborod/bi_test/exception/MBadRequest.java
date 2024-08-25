package ru.pashabezborod.bi_test.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public abstract class MBadRequest extends MException {

    private final static HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    protected MBadRequest(String message) {
        super(message, STATUS);
    }

    protected MBadRequest(String message, ExceptionField field, String data, Throwable cause) {
        super(message, STATUS, field, data, cause);
    }

    protected MBadRequest(String message, ExceptionField field, String data) {
        super(message, STATUS, field, data);
    }

    protected MBadRequest(String message, Map<ExceptionField, String> causes, Throwable cause) {
        super(message, STATUS, causes, cause);
    }
}
