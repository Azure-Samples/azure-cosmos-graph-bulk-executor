// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.annotations.*;
import com.azure.graph.bulk.impl.annotations.GremlinEdgeVertex.Direction;
import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import com.azure.graph.bulk.impl.model.ObjectConversionException;
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
    public static com.azure.graph.bulk.impl.model.GremlinEdge toGremlinEdge(Object from) {
        Class<?> clazz = from.getClass();

        com.azure.graph.bulk.impl.model.GremlinEdge converted = new com.azure.graph.bulk.impl.model.GremlinEdge();

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
        Class<GremlinEdge> annotationClass = GremlinEdge.class;
        if (!clazz.isAnnotationPresent(annotationClass)) {
            throw new IllegalArgumentException(
                    "Class " + clazz.getSimpleName() + " is missing GremlinEdge annotation");
        }

        String edgeLabel = clazz.getAnnotation(annotationClass).label();

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
     */
    private static void setLabelFromGetter(Class<?> clazz,
                                           com.azure.graph.bulk.impl.model.GremlinEdge results,
                                           Object from) {
        for (Method method : MethodUtils.getMethodsWithAnnotation(clazz, GremlinLabelGetter.class)) {
            if (isStatic(method.getModifiers()) || !method.canAccess(from))
                continue; // method is not accessible, attempts to call it will fail.
            String edgeLabel = null;

            try {
                edgeLabel = (String) method.invoke(from);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ObjectConversionException(e);
            }

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
    private static void setFieldValues(Class<?> clazz,
                                       com.azure.graph.bulk.impl.model.GremlinEdge results,
                                       Object from) {
        for (Field field : FieldUtils.getAllFields(clazz)) {
            if (isStatic(field.getModifiers()) || !field.canAccess(from)) continue; // Field is not accessible
            setIdValue(field, results, from);
            if (results.getLabel() == null || results.getLabel().isBlank()) {
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
     */
    private static void setLabel(Field field,
                                 com.azure.graph.bulk.impl.model.GremlinEdge results,
                                 Object from) {
        if (!field.isAnnotationPresent(GremlinLabel.class)) return;

        try {
            results.setLabel((String) field.get(from));
        } catch (IllegalAccessException e) {
            throw new ObjectConversionException(e);
        }
    }

    /**
     * Sets the id value if the field has been marked with the GremlinId annotation
     *
     * @param field   the field definition to extract the value using
     * @param results the GremlinEdge being updated
     * @param from    the instance of the class to extract the values from
     */
    private static void setIdValue(Field field,
                                   com.azure.graph.bulk.impl.model.GremlinEdge results,
                                   Object from) {
        if (!field.isAnnotationPresent(GremlinId.class)) return;

        try {
            results.setId((String) field.get(from));
        } catch (IllegalAccessException e) {
            throw new ObjectConversionException(e);
        }

        if (results.getId() == null || results.getId().isBlank()) {
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
     */
    private static void setSourceVertexValues(Class<?> clazz, Field field,
                                              com.azure.graph.bulk.impl.model.GremlinEdge results,
                                              Object from) {
        if (field.isAnnotationPresent(GremlinEdgeVertex.class)
                && field.getAnnotation(GremlinEdgeVertex.class).Direction() == Direction.SOURCE) {
            Class<GremlinEdge> annotationClass = GremlinEdge.class;
            GremlinEdge edgeAnnotation = clazz.getAnnotation(annotationClass);

            GremlinEdgeVertexInfo vertexInfo = getVertexInfo(field, from);

            com.azure.graph.bulk.impl.model.GremlinPartitionKey pk =
                    com.azure.graph.bulk.impl.model.GremlinPartitionKey.builder()
                            .fieldName(edgeAnnotation.partitionKeyFieldName().isBlank()
                                    ? field.getName()
                                    : edgeAnnotation.partitionKeyFieldName())
                            .value(vertexInfo.getPartitionKey())
                            .build();

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
     */
    private static void setDestinationVertexValues(Field field,
                                                   com.azure.graph.bulk.impl.model.GremlinEdge results,
                                                   Object from) {
        if (field.isAnnotationPresent(GremlinEdgeVertex.class)
                && field.getAnnotation(GremlinEdgeVertex.class).Direction() == Direction.DESTINATION) {

            GremlinEdgeVertexInfo vertexInfo = getVertexInfo(field, from);

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
     */
    private static GremlinEdgeVertexInfo getVertexInfo(Field field, Object from) {
        Object vertex = null;
        try {
            vertex = field.get(from);
        } catch (IllegalAccessException e) {
            throw new ObjectConversionException(e);
        }

        if (vertex instanceof GremlinEdgeVertexInfo) return (GremlinEdgeVertexInfo) vertex;

        Class<?> vertexClass = vertex.getClass();

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
     */
    private static void setPropertyMap(Field field,
                                       com.azure.graph.bulk.impl.model.GremlinEdge results,
                                       Object from) {
        if (!field.isAnnotationPresent(GremlinPropertyMap.class)) return;

        Object value = null;
        try {
            value = field.get(from);
        } catch (IllegalAccessException e) {
            throw new ObjectConversionException(e);
        }
        if (value instanceof Map) {
            //noinspection unchecked
            Map<String, Object> properties = (Map<String, Object>) value;
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
     */
    private static void setPropertyValues(Field field,
                                          com.azure.graph.bulk.impl.model.GremlinEdge results,
                                          Object from) {
        if (field.isAnnotationPresent(GremlinIgnore.class) ||
                field.isAnnotationPresent(GremlinId.class) ||
                field.isAnnotationPresent(GremlinPartitionKey.class) ||
                field.isAnnotationPresent(GremlinLabel.class) ||
                field.isAnnotationPresent(GremlinPropertyMap.class) ||
                field.isAnnotationPresent(GremlinEdgeVertex.class)
        ) return;

        Object value = null;
        try {
            value = field.get(from);
        } catch (IllegalAccessException e) {
            throw new ObjectConversionException(e);
        }
        if (value == null) return;

        String key = field.getName();
        if (field.isAnnotationPresent(GremlinProperty.class)) {
            key = field.getAnnotation(GremlinProperty.class).name();
        }
        results.addProperty(key, value);
    }
}
