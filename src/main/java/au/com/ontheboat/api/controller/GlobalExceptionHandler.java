package au.com.ontheboat.api.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationFieldError>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ValidationFieldError> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> new ValidationFieldError(e.getField(), e.getDefaultMessage(), e.getRejectedValue()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    @Data
    @AllArgsConstructor
    private class ValidationFieldError {
        private String field;
        private String message;
        private Object value;
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        return Map.of("errors", errors);
    }
}
