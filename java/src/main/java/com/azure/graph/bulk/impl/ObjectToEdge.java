// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.annotations.*;
import com.azure.graph.bulk.impl.annotations.GremlinEdgeVertex.Direction;
import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static java.lang.reflect.Modifier.*;

public final class ObjectToEdge {
    private ObjectToEdge() {
        throw new IllegalStateException("Utility class, should not be constructed");
    }

    /**
     * Converts an object defined by a class that has GremlinEdge annotations defined into an instance of a GremlinEdge
     *
     * @param from object to convert into a GremlinEdge
     * @return An instance of the GremlinEdge object based on the values extracted from the object provided
     */
    @SneakyThrows
    public static com.azure.graph.bulk.impl.model.GremlinEdge toGremlinEdge(Object from) {
        Class<?> clazz = from.getClass();

        var converted = new com.azure.graph.bulk.impl.model.GremlinEdge();

        setLabelFromClass(clazz, converted);
        if (converted.getLabel() == null) {
            setLabelFromGetter(clazz, converted, from);
        }
        setFieldValues(clazz, converted, from);

        return converted;
    }


    /**
     * Sets the label value from the GremlinEdge class level annotation if present
     *
     * @param clazz   the class of the object being used
     * @param results the GremlinEdge being updated
     */
    private static void setLabelFromClass(Class<?> clazz, com.azure.graph.bulk.impl.model.GremlinEdge results) {
        var annotationClass = GremlinEdge.class;
        if (!clazz.isAnnotationPresent(annotationClass)) {
            throw new IllegalArgumentException(
                    "Class " + clazz.getSimpleName() + " is missing GremlinEdge annotation");
        }

        var edgeLabel = clazz.getAnnotation(annotationClass).label();

        if (!edgeLabel.isBlank()) {
            results.setLabel(edgeLabel);
        }
    }

    /**
     * Sets the label value from the results of calling the method marked with a GremlinLabelGetter annotation
     *
     * @param clazz   the class of the object being used
     * @param results the GremlinEdge being updated
     * @param from    the instance of the class to extract the values from
     * @throws InvocationTargetException When the method executed throws an exceptions
     * @throws IllegalAccessException    Guards in place to prevent exception from being thrown
     */
    private static void setLabelFromGetter(Class<?> clazz, com.azure.graph.bulk.impl.model.GremlinEdge results, Object from)
            throws InvocationTargetException, IllegalAccessException {
        for (Method method : MethodUtils.getMethodsWithAnnotation(clazz, GremlinLabelGetter.class)) {
            if (isStatic(method.getModifiers()) || !method.canAccess(from))
                continue; // method is not accessible, attempts to call it will fail.
            var edgeLabel = (String) method.invoke(from);

            if (!edgeLabel.isBlank()) {
                results.setLabel(edgeLabel);
            }
        }
    }

    /**
     * Iterates throw the fields defined on the class and extracts values from fields marked with the
     * appropriate annotations
     *
     * @param clazz   the class of the object being used
     * @param results the GremlinEdge being updated
     * @param from    the instance of the class to extract the values from
     */
    @SneakyThrows
    private static void setFieldValues(Class<?> clazz, com.azure.graph.bulk.impl.model.GremlinEdge results, Object from) {
        for (Field field : FieldUtils.getAllFields(clazz)) {
            if (isStatic(field.getModifiers()) || !field.canAccess(from)) continue; // Field is not accessible
            setIdValue(field, results, from);
            if (results.getLabel() == null) {
                setLabel(field, results, from);
            }
            setSourceVertexValues(clazz, field, results, from);
            setDestinationVertexValues(field, results, from);
            setPropertyMap(field, results, from);
            setPropertyValues(field, results, from);
        }
    }

    /**
     * Sets the value of the label if the field has been marked with a @GremlinLabel annotation.
     * Will override any value already set for the label by either the class annotation or the getter method
     *
     * @param field   the field definition to extract the value using
     * @param results the GremlinEdge being updated
     * @param from    the instance of the class to extract the values from
     * @throws IllegalAccessException When the field is not accessible from this utility
     */
    private static void setLabel(Field field, com.azure.graph.bulk.impl.model.GremlinEdge results, Object from) throws IllegalAccessException {
        if (!field.isAnnotationPresent(GremlinLabel.class)) return;

        results.setLabel((String) field.get(from));
    }

    /**
     * Sets the id value if the field has been marked with the GremlinId annotation
     *
     * @param field   the field definition to extract the value using
     * @param results the GremlinEdge being updated
     * @param from    the instance of the class to extract the values from
     * @throws IllegalAccessException When the field is not accessible from this utility
     */
    private static void setIdValue(Field field, com.azure.graph.bulk.impl.model.GremlinEdge results, Object from)
            throws IllegalAccessException {
        if (!field.isAnnotationPresent(GremlinId.class)) return;

        results.setId((String) field.get(from));

        if (results.getId() == null) {
            throw new IllegalArgumentException(
                    "GremlinId cannot be null, please ensure " + field.getName() + " is populated.");
        }
    }

    /**
     * Sets the values (PartitionKey and sourceVertexInfo) from the field marked with a GremlinEdgeVertex annotation
     * and the direction is SOURCE. For the partition key's field name, it will default to the value provided by the
     * GremlinEdge annotation's partitionKeyFieldName if present, otherwise will use the name of the field that has
     * been marked as the source vertex.
     *
     * @param clazz   the class of the object being used
     * @param field   the field definition to extract the value using
     * @param results the GremlinEdge being updated
     * @param from    the instance of the class to extract the values from
     * @throws IllegalAccessException When the field is not accessible from this utility
     */
    private static void setSourceVertexValues(Class<?> clazz, Field field, com.azure.graph.bulk.impl.model.GremlinEdge results, Object from)
            throws IllegalAccessException {
        if (field.isAnnotationPresent(GremlinEdgeVertex.class)
                && field.getAnnotation(GremlinEdgeVertex.class).Direction() == Direction.SOURCE) {
            var annotationClass = GremlinEdge.class;
            var edgeAnnotation = clazz.getAnnotation(annotationClass);

            var vertexInfo = getVertexInfo(field, from);

            var pk = new com.azure.graph.bulk.impl.model.GremlinPartitionKey(
                    edgeAnnotation.partitionKeyFieldName().isBlank() ? field.getName() : edgeAnnotation.partitionKeyFieldName(),
                    vertexInfo.getPartitionKey()
            );

            results.setPartitionKey(pk);
            results.setSourceVertexInfo(vertexInfo);
        }
    }

    /**
     * Sets the values of the destinationVertexInfo field from the field marked with a GremlinEdgeVertex annotation
     * and the direction is DESTINATION
     *
     * @param field   the field definition to extract the value using
     * @param results the GremlinEdge being updated
     * @param from    the instance of the class to extract the values from
     * @throws IllegalAccessException When the field is not accessible from this utility
     */
    private static void setDestinationVertexValues(Field field, com.azure.graph.bulk.impl.model.GremlinEdge results, Object from)
            throws IllegalAccessException {
        if (field.isAnnotationPresent(GremlinEdgeVertex.class)
                && field.getAnnotation(GremlinEdgeVertex.class).Direction() == Direction.DESTINATION) {
            var vertexInfo = getVertexInfo(field, from);

            results.setDestinationVertexInfo(vertexInfo);
        }
    }

    /**
     * Extracts the GremlinEdgeVertexInfo value from the source object. If the value is already a
     * GremlinEdgeVertexInfo, it will use that, otherwise will use reflection to extract the values
     * from the Vertex object
     *
     * @param field the field definition to extract the value using
     * @param from  the instance of the class to extract the values from
     * @return the extracted GremlinEdgeVertexInfo values
     * @throws IllegalAccessException When the field is not accessible from this utility
     */
    private static GremlinEdgeVertexInfo getVertexInfo(Field field, Object from) throws IllegalAccessException {
        var vertex = field.get(from);

        if (vertex instanceof GremlinEdgeVertexInfo) return (GremlinEdgeVertexInfo) vertex;

        var vertexClass = vertex.getClass();

        if (!vertexClass.isAnnotationPresent(GremlinVertex.class)) {
            throw new IllegalArgumentException(
                    "Class " + vertexClass.getSimpleName() + " is missing GremlinVertex annotation");
        }

        return GremlinEdgeVertexInfo.fromGremlinVertex(vertex);
    }

    /**
     * Concatenates the properties from fields marked with the GremlinPropertyMap annotation to the target results
     * Properties bag
     *
     * @param field   the field definition to extract the value using
     * @param results the GremlinEdge being updated
     * @param from    the instance of the class to extract the values from
     * @throws IllegalAccessException When the field is not accessible from this utility
     */
    private static void setPropertyMap(Field field, com.azure.graph.bulk.impl.model.GremlinEdge results, Object from) throws IllegalAccessException {
        if (!field.isAnnotationPresent(GremlinPropertyMap.class)) return;

        var value = field.get(from);
        if (value instanceof Map) {
            //TODO: Determine impacts to forcing the Data Type of the value to at least be serializable
            //noinspection unchecked
            var properties = (Map<String, Object>) value;
            properties.forEach((k, v) -> {
                if (v != null) {
                    results.addProperty(k, v);
                }
            });
        }
    }

    /**
     * Adds the field to the properties bag when the field hasn't been marked with other Gremlin related
     * annotations and doesn't have a GremlinIgnore annotation
     *
     * @param field   the field definition to extract the value using
     * @param results the GremlinEdge being updated
     * @param from    the instance of the class to extract the values from
     * @throws IllegalAccessException When the field is not accessible from this utility
     */
    private static void setPropertyValues(Field field, com.azure.graph.bulk.impl.model.GremlinEdge results, Object from)
            throws IllegalAccessException {
        if (field.isAnnotationPresent(GremlinIgnore.class) ||
                field.isAnnotationPresent(GremlinId.class) ||
                field.isAnnotationPresent(GremlinPartitionKey.class) ||
                field.isAnnotationPresent(GremlinLabel.class) ||
                field.isAnnotationPresent(GremlinPropertyMap.class) ||
                field.isAnnotationPresent(GremlinEdgeVertex.class)
        ) return;

        var value = field.get(from);
        if (value == null) return;

        var key = field.getName();
        if (field.isAnnotationPresent(GremlinProperty.class)) {
            key = field.getAnnotation(GremlinProperty.class).name();
        }
        results.addProperty(key, value);
    }
}
