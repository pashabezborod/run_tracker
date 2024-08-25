package ru.pashabezborod.bi_test.exception.run;

import ru.pashabezborod.bi_test.exception.MBadRequest;

public class InvalidRunStartTimeException extends MBadRequest {

    private final static String MESSAGE = "Invalid run start time. It can not be before last run start or end time.";

    public InvalidRunStartTimeException() {
        super(MESSAGE);
    }
}
