// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.annotations;

import com.azure.graph.bulk.impl.annotations.GremlinEdgeVertex.Direction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EdgeAnnotationValidator {
    public static final String GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION = "GremlinLabel and GremlinLabelGetter " +
            "annotations can only be used when GremlinEdge class annotation hasn't set a label value.";
    public static final String GREMLIN_LABEL_INVALID = "GremlinLabel and GremlinLabelGetter required to be present " +
            "on only one field or method when there is no label set on the GremlinEdge class annotation.";
    public static final String GREMLIN_EDGE_VERTEX_MISSING = "GremlinEdge's require one field marked with " +
            "GremlinEdgeVertex annotation and the direction %s.";
    public static final String GREMLIN_EDGE_VERTEX_TO_MANY = "Only one field can be marked with the " +
            "GremlinEdgeVertex annotation and the direction %s.";
    public static final String GREMLIN_EDGE_ID = "GremlinEdge classes can only have one field with the GremlinId " +
            "annotation.";
    public static final String GREMLIN_EDGE_PARTITION_KEY = "GremlinEdge classes construct the Partition Key from " +
            "the source vertex and the partitionKeyName value in the GremlinEdge call annotation.";

    private final Map<String, List<String>> validatedClasses = new HashMap<>();

    public List<String> validate(Class<?> clazz) {
        if (validatedClasses.containsKey(clazz.getName()))
            return validatedClasses.get(clazz.getName());

        List<String> results = new ArrayList<>();

        Stream<Class<? extends Annotation>> fieldAnnotations =
                Stream.of(GremlinLabel.class, GremlinEdgeVertex.class, GremlinPartitionKey.class, GremlinId.class);
        Map<Class<? extends Annotation>, Integer> fieldAnnotationCounts = getFieldAnnotationCounts(clazz,
                fieldAnnotations);

        Stream<Class<? extends Annotation>> methodAnnotations =
                Stream.of(GremlinLabelGetter.class);
        Map<Class<? extends Annotation>, Integer> methodAnnotationCounts = getMethodAnnotationCounts(clazz,
                methodAnnotations);

        validateLabel(clazz, results, fieldAnnotationCounts, methodAnnotationCounts);
        validateId(results, fieldAnnotationCounts);
        validateEdges(clazz, results);
        validatePartitionKey(results, fieldAnnotationCounts);

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

        GremlinEdge classAnnotation = clazz.getAnnotation(GremlinEdge.class);

        if (labelAnnotationCount > 0 && !classAnnotation.label().isBlank())
            results.add(GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION);

        if (labelAnnotationCount != 1 && classAnnotation.label().isBlank())
            results.add(GREMLIN_LABEL_INVALID);
    }

    private void validateId(List<String> results, Map<Class<? extends Annotation>, Integer> fieldAnnotationCounts) {
        if (fieldAnnotationCounts.get(GremlinId.class) > 1)
            results.add(GREMLIN_EDGE_ID);
    }

    private void validatePartitionKey(
            List<String> results, Map<Class<? extends Annotation>,
            Integer> fieldAnnotationCounts) {
        if (fieldAnnotationCounts.get(GremlinPartitionKey.class) > 0)
            results.add(GREMLIN_EDGE_PARTITION_KEY);
    }

    private void validateEdges(
            Class<?> clazz,
            List<String> results) {

        Map<GremlinEdgeVertex.Direction, Long> directionCounts = Arrays.stream(clazz.getFields())
                .filter(field -> field.isAnnotationPresent(GremlinEdgeVertex.class))
                .map(field -> field.getAnnotation(GremlinEdgeVertex.class).direction())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        validateEdgeDirection(results, Direction.DESTINATION, directionCounts);
        validateEdgeDirection(results, Direction.SOURCE, directionCounts);
    }

    private void validateEdgeDirection(
            List<String> results,
            GremlinEdgeVertex.Direction direction,
            Map<GremlinEdgeVertex.Direction, Long> directionCounts) {

        Long count = directionCounts.getOrDefault(direction, 0l);
        if (count == 0) {
            results.add(String.format(GREMLIN_EDGE_VERTEX_MISSING, direction.name()));
        }
        if (count > 1) {
            results.add(String.format(GREMLIN_EDGE_VERTEX_TO_MANY, direction.name()));
        }
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
