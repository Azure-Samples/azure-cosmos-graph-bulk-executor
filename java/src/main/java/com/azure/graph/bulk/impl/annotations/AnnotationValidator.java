package com.azure.graph.bulk.impl.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AnnotationValidator {
    public static final String GREMLIN_PARTITION_KEY_INVALID =
            "GremlinPartitionKey annotation is required to be present on only one field.";
    public static final String GREMLIN_ID_INVALID =
            "GremlinId annotation is required to be present on only one field.";
    public static final String GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION = "GremlinLabel and GremlinLabelGetter " +
            "annotations can only be used when GremlinVertex class annotation hasn't set a label value.";
    public static final String GREMLIN_LABEL_INVALID = "GremlinLabel and GremlinLabelGetter required to be present " +
            "on only one field or method when there is no label set on the GremlinVertex class annotation.";
    
    private Map<String, List<String>> validatedVertexClasses = new HashMap<>();
    private Map<String, List<String>> validatedEdgeClasses = new HashMap<>();


    public List<String> validateVertexClass(Class<?> clazz) {
        if (validatedVertexClasses.containsKey(clazz.getName()))
            return validatedVertexClasses.get(clazz.getName());

        List<String> results = new ArrayList<>();

        Stream<Class<? extends Annotation>> fieldAnnotations =
                Stream.of(GremlinId.class, GremlinPartitionKey.class, GremlinLabel.class);
        Map<Class<? extends Annotation>, Integer> fieldAnnotationCounts = getFieldAnnotationCounts(clazz,
                fieldAnnotations);

        Stream<Class<? extends Annotation>> methodAnnotations =
                Stream.of(GremlinLabelGetter.class);
        Map<Class<? extends Annotation>, Integer> methodAnnotationCounts = getMethodAnnotationCounts(clazz,
                methodAnnotations);

        if (fieldAnnotationCounts.get(GremlinPartitionKey.class) != 1)
            results.add(GREMLIN_PARTITION_KEY_INVALID);

        if (fieldAnnotationCounts.get(GremlinId.class) != 1)
            results.add(GREMLIN_ID_INVALID);

        int labelAnnotationCount = fieldAnnotationCounts.get(GremlinLabel.class) +
                methodAnnotationCounts.get(GremlinLabelGetter.class);

        if (labelAnnotationCount > 0 && !clazz.getAnnotation(GremlinVertex.class).label().isBlank())
            results.add(GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION);

        if (labelAnnotationCount != 1 && clazz.getAnnotation(GremlinVertex.class).label().isBlank())
            results.add(GREMLIN_LABEL_INVALID);

        validatedVertexClasses.put(clazz.getName(), results);

        return results;
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
