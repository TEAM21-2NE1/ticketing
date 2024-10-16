package com.ticketing.performance.presentation.dto.performance;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AgeLimitValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AgeLimitConstraint {
    String message() default "유효하지 않은 나이 제한입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
