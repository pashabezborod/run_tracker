package ru.pashabezborod.bi_test.exception.run;

import ru.pashabezborod.bi_test.exception.MBadRequest;

public class LastRunNotFinishedException extends MBadRequest {

    private final static String MESSAGE = "Last run not finished. Finish it before start new";

    public LastRunNotFinishedException() {
        super(MESSAGE);
    }
}
