package ru.pashabezborod.bi_test.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.*;

@Getter
@JsonSerialize(using = MException.Serializer.class)
public class MException extends  Exception {

    private final HttpStatus status;
    private Map<ExceptionField, String> causes = new HashMap<>();

    protected MException(String message, HttpStatus httpStatus) {
        super(message);
        this.status = httpStatus;
    }

    protected MException(String message, HttpStatus httpStatus, ExceptionField field, String data, Throwable cause) {
        super(message, cause);
        this.status = httpStatus;
        this.causes = Collections.singletonMap(field, data);
    }

    protected MException(String message, HttpStatus httpStatus, ExceptionField field, String data) {
        super(message);
        this.status = httpStatus;
        this.causes = Collections.singletonMap(field, data);
    }

    protected MException(String message, HttpStatus httpStatus, Map<ExceptionField, String> causes, Throwable cause) {
        super(message, cause);
        this.status = httpStatus;
        this.causes = causes;
    }

    @Override
    public String toString() {
        if (causes.isEmpty()) return getMessage();
        final StringJoiner result = new StringJoiner("\n");
        result.add(getMessage());
        causes.forEach((k, v) -> result.add(k.name() + ": " + v));
        return result.toString();
    }

    static class Serializer extends JsonSerializer<MException> {

        @Override
        public void serialize(MException value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject(value.getMessage());
            gen.writeFieldName("message");
            gen.writeString(value.getMessage());
            if (value.getCauses().isEmpty()) return;
            final Map<String, String> result = new HashMap<>();
            value.getCauses().forEach((k, v) -> result.put(k.name(), v));
            gen.writeFieldName("causes");
            gen.writeObject(result);
        }
    }
}

