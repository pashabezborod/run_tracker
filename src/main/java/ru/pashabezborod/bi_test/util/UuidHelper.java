package ru.pashabezborod.bi_test.util;

import ru.pashabezborod.bi_test.exception.UuidNotValidException;

import java.util.UUID;

public class UuidHelper {

    public static UUID get(String id) throws UuidNotValidException {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new UuidNotValidException(id, e);
        }
    }
}
