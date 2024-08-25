package ru.pashabezborod.bi_test.config;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.pashabezborod.bi_test.exception.ExceptionField;
import ru.pashabezborod.bi_test.exception.MException;
import ru.pashabezborod.bi_test.exception.NoRequiredParameterException;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static ru.pashabezborod.bi_test.exception.ExceptionField.PARAMETER_NAME;
import static ru.pashabezborod.bi_test.exception.ExceptionField.PARAMETER_TYPE;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {

    @ExceptionHandler(MException.class)
    ResponseEntity<Object> handleMException(MException e) {
        log.warn(e.toString(), e);
        return ResponseEntity.status(e.getStatus()).body(e);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<Object> handleMissingParameter(MissingServletRequestParameterException e) {
        final Map<ExceptionField, String> map = new HashMap<>() {{
            put(PARAMETER_NAME, e.getParameterName());
            put(PARAMETER_TYPE, e.getParameterType());
        }};
        return handleMException(new NoRequiredParameterException(map, e));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Object> handleValidation(ConstraintViolationException e) {
        final Map<String, String> causes = new HashMap<>();
        e.getConstraintViolations().forEach(it -> {
                    final String path = it.getPropertyPath().toString();
                    causes.put(path.substring(path.lastIndexOf(".") + 1), it.getMessage());
                }
        );
        ViolationObject object = new ViolationObject("Validation error", causes);
        log.warn(e.toString(), e);
        return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(object);
    }

    private record ViolationObject(
            String message,
            Map<String, String> causes) {

        @Override
        public String toString() {
            if (causes.isEmpty()) return message;
            final StringJoiner result = new StringJoiner("\n");
            result.add(message);
            causes.forEach((k, v) -> result.add(k + ": " + v));
            return result.toString();
        }
    }

}
