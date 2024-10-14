package com.ticketing.performance.common.exception;

import lombok.*;
import org.springframework.validation.BindingResult;

import java.util.List;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor

public class FieldError {

    private final String field;
    private final String message;

    public static List<FieldError> of(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage())
                ).toList();
    }

}