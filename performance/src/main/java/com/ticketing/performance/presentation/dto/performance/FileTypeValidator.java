package com.ticketing.performance.presentation.dto.performance;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileTypeValidator implements ConstraintValidator<FileTypeConstraint, MultipartFile> {
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "image/jpg"};

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null) return false;
        String contentType = file.getContentType();
        for (String type : ALLOWED_TYPES) {
            if (type.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }
}
