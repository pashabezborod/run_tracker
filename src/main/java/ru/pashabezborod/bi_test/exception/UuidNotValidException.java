package ru.pashabezborod.bi_test.exception;

public class UuidNotValidException extends MBadRequest {

    private static final String MESSAGE = "Provided UUID is not valid";
    private static final ExceptionField FIELD = ExceptionField.UUID;

    public UuidNotValidException(String uuid, Throwable throwable) {
        super(MESSAGE, FIELD, uuid, throwable);
    }
}
