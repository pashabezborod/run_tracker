package ru.pashabezborod.bi_test.exception.run;

import ru.pashabezborod.bi_test.exception.MBadRequest;

public class InvalidRunFinishTimeException extends MBadRequest {

    private final static String MESSAGE = "Invalid run finish time. It can not be before last run start time.";

    public InvalidRunFinishTimeException() {
        super(MESSAGE);
    }
}
