package com.azure.graph.bulk.impl.model;

import java.util.List;

public class AnnotationValidationException extends RuntimeException {
    public AnnotationValidationException(Class<?> clazz, List<String> validationErrors) {
        super(String.format("%s failed validation with the following errors %s",
                clazz.getName(),
                validationErrors));
    }
}
