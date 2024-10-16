package com.ticketing.performance.presentation.dto.performance;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class AgeLimitValidator implements ConstraintValidator<AgeLimitConstraint, Integer> {
    private static final Set<Integer> VALID_AGE_LIMITS = Set.of(0, 7, 12, 15, 19);

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == null || VALID_AGE_LIMITS.contains(value);
    }
}