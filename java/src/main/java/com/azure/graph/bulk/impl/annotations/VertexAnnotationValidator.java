package com.azure.graph.bulk.impl.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class VertexAnnotationValidator {
    public static final String GREMLIN_PARTITION_KEY_INVALID =
            "GremlinPartitionKey annotation is required to be present on only one field.";
    public static final String GREMLIN_ID_INVALID =
            "GremlinId annotation is required to be present on one and only one field.";
    public static final String GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION = "GremlinLabel and GremlinLabelGetter " +
            "annotations can only be used when GremlinVertex class annotation hasn't set a label value.";
    public static final String GREMLIN_LABEL_INVALID = "GremlinLabel and GremlinLabelGetter required to be present " +
            "on only one field or method when there is no label set on the GremlinVertex class annotation.";
    public static final String GREMLIN_EDGES_MISSING = "GremlinEdge annotation is required on two fields.";

    private final Map<String, List<String>> validatedClasses = new HashMap<>();


    public List<String> validateVertexClass(Class<?> clazz) {
        if (validatedClasses.containsKey(clazz.getName()))
            return validatedClasses.get(clazz.getName());

        List<String> results = new ArrayList<>();

        Stream<Class<? extends Annotation>> fieldAnnotations =
                Stream.of(GremlinId.class, GremlinPartitionKey.class, GremlinLabel.class);
        Map<Class<? extends Annotation>, Integer> fieldAnnotationCounts = getFieldAnnotationCounts(clazz,
                fieldAnnotations);

        Stream<Class<? extends Annotation>> methodAnnotations =
                Stream.of(GremlinLabelGetter.class);
        Map<Class<? extends Annotation>, Integer> methodAnnotationCounts = getMethodAnnotationCounts(clazz,
                methodAnnotations);

        validatePartitionKey(results, fieldAnnotationCounts);
        validateId(results, fieldAnnotationCounts);
        validateLabel(clazz, results, fieldAnnotationCounts, methodAnnotationCounts);

        validatedClasses.put(clazz.getName(), results);

        return results;
    }

    private void validateLabel(
            Class<?> clazz,
            List<String> results,
            Map<Class<? extends Annotation>, Integer> fieldAnnotationCounts,
            Map<Class<? extends Annotation>, Integer> methodAnnotationCounts) {

        int labelAnnotationCount = fieldAnnotationCounts.get(GremlinLabel.class) +
                methodAnnotationCounts.get(GremlinLabelGetter.class);

        GremlinVertex classAnnotation = clazz.getAnnotation(GremlinVertex.class);

        if (labelAnnotationCount > 0 && !classAnnotation.label().isBlank())
            results.add(GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION);

        if (labelAnnotationCount != 1 && classAnnotation.label().isBlank())
            results.add(GREMLIN_LABEL_INVALID);
    }

    private void validatePartitionKey(List<String> results, Map<Class<? extends Annotation>, Integer> fieldAnnotationCounts) {
        if (fieldAnnotationCounts.get(GremlinPartitionKey.class) != 1)
            results.add(GREMLIN_PARTITION_KEY_INVALID);
    }

    private void validateId(List<String> results, Map<Class<? extends Annotation>, Integer> fieldAnnotationCounts) {
        if (fieldAnnotationCounts.get(GremlinId.class) != 1)
            results.add(GREMLIN_ID_INVALID);
    }

    private Map<Class<? extends Annotation>, Integer> getFieldAnnotationCounts(
            Class<?> clazz,
            Stream<Class<? extends Annotation>> annotationsToCount) {

        Map<Class<? extends Annotation>, Integer> annotationCounts = new HashMap<>();

        annotationsToCount.forEach(annotation -> annotationCounts.put(annotation, 0));

        for (Field field : clazz.getFields()) {
            for (Map.Entry<Class<? extends Annotation>, Integer> annotation :
                    annotationCounts.entrySet()) {
                if (field.isAnnotationPresent(annotation.getKey())) {
                    annotation.setValue(annotation.getValue() + 1);
                }
            }
        }

        return annotationCounts;
    }

    private Map<Class<? extends Annotation>, Integer> getMethodAnnotationCounts(
            Class<?> clazz,
            Stream<Class<? extends Annotation>> annotationsToCount) {

        Map<Class<? extends Annotation>, Integer> annotationCounts = new HashMap<>();

        annotationsToCount.forEach(annotation -> {
            annotationCounts.put(annotation, 0);
        });

        for (Method method : clazz.getMethods()) {
            for (Map.Entry<Class<? extends Annotation>, Integer> annotation :
                    annotationCounts.entrySet()) {
                if (method.isAnnotationPresent(annotation.getKey())) {
                    annotation.setValue(annotation.getValue() + 1);
                }
            }
        }

        return annotationCounts;
    }
}
