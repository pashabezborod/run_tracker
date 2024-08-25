package ru.pashabezborod.bi_test.exception;

public class UserNotFoundException extends MBadRequest {

    private final static String MESSAGE = "User not found";
    private final static ExceptionField FIELD = ExceptionField.USER_ID;

    public UserNotFoundException(String userId) {
        super(MESSAGE, FIELD, userId);
    }
}
