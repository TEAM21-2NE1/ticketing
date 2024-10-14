package com.ticketing.review.common.exception;


import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;

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