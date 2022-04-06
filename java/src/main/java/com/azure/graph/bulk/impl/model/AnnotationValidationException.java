// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.model;

import java.util.List;
import java.util.stream.Collectors;

public class AnnotationValidationException extends RuntimeException {
    public AnnotationValidationException(Class<?> clazz, List<String> validationErrors) {
        super(String.format("%s failed validation with the following errors:%n%n* %s",
                clazz.getName(),
                validationErrors.stream().collect(Collectors.joining(String.format("%n* ")))));
    }
}
