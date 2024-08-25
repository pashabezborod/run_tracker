package ru.pashabezborod.bi_test.exception.run;

import ru.pashabezborod.bi_test.exception.MBadRequest;

public class NoActiveRunException extends MBadRequest {

    private static final String MESSAGE = "User does not have active run";

    public NoActiveRunException() {
        super(MESSAGE);
    }
}
