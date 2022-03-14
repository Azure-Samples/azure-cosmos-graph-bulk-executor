package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.annotations.GremlinId;
import com.azure.graph.bulk.impl.annotations.GremlinIgnore;
import com.azure.graph.bulk.impl.annotations.GremlinLabel;
import com.azure.graph.bulk.impl.annotations.GremlinLabelGetter;
import com.azure.graph.bulk.impl.annotations.GremlinPartitionKey;
import com.azure.graph.bulk.impl.annotations.GremlinProperty;
import com.azure.graph.bulk.impl.annotations.GremlinPropertyMap;
import com.azure.graph.bulk.impl.annotations.GremlinVertex;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.lang.reflect.Modifier.*;

public final class ObjectToVertex {
    private ObjectToVertex() {
        throw new IllegalStateException("Utility class, should not be constructed");
    }

    /**
     * Converts an object defined by a class that has GremlinVertex annotations defined into an
     * instance of a GremlinVertex
     *
     * @param from object to convert into a GremlinVertex
     * @return An instance of the GremlinVertex object based on the values extracted from the object provided
     */
    @SneakyThrows
    // InvocationTargetException should never be thrown due to not being able to see methods that are not public
    public static com.azure.graph.bulk.impl.model.GremlinVertex toGremlinVertex(Object from) {
        Class<?> clazz = from.getClass();

        var results = com.azure.graph.bulk.impl.model.GremlinVertex.builder()
                .properties(new HashMap<>())
                .build();

        setLabelFromClass(clazz, results);

        if (results.getLabel() == null) {
            setLabelFromGetter(clazz, results, from);
        }

        setFieldValues(clazz, results, from);
        return results;
    }

    /**
     * Sets the label value from the GremlinVertex class level annotation if present
     *
     * @param clazz   the class of the object being used
     * @param results the GremlinVertex being updated
     */
    private static void setLabelFromClass(Class<?> clazz, com.azure.graph.bulk.impl.model.GremlinVertex results) {
        var annotationClass = GremlinVertex.class;
        if (!clazz.isAnnotationPresent(annotationClass)) {
            throw new IllegalArgumentException(
                    "Class " + clazz.getSimpleName() + " is missing GremlinVertex annotation");
        }

        var vertexAnnotation = clazz.getAnnotation(annotationClass);
        var label = vertexAnnotation.label();

        if (!label.isBlank()) {
            results.setLabel(label);
        }
        results.setLabel(label);
    }

    /**
     * Iterates throw the fields defined on the class and extracts values from fields marked with the
     * appropriate annotations
     *
     * @param clazz   the class of the object being used
     * @param results the GremlinVertex being updated
     * @param from    the instance of the class to extract the values from
     */
    @SneakyThrows
    private static void setFieldValues(Class<?> clazz, com.azure.graph.bulk.impl.model.GremlinVertex results, Object from) {
        for (Field field : FieldUtils.getAllFields(clazz)) {
            if (isStatic(field.getModifiers()) || !field.canAccess(from)) continue; // Field is not accessible
            setIdValue(field, results, from);
            if (results.getLabel() == null) {
                setLabel(field, results, from);
            }
            setPartitionKey(field, results, from);
            setPropertyMap(field, results, from);
            setPropertyValues(field, results, from);
        }
    }

    /**
     * Sets the id value if the field has been marked with the GremlinId annotation
     *
     * @param field   the field definition to extract the value using
     * @param results the GremlinVertex being updated
     * @param from    the instance of the class to extract the values from
     * @throws IllegalAccessException When the field is not accessible from this utility
     */
    private static void setIdValue(Field field, com.azure.graph.bulk.impl.model.GremlinVertex results, Object from)
            throws IllegalAccessException {

        if (field.isAnnotationPresent(GremlinId.class)) {
            results.setId((String) field.get(from));

            if (results.getId() == null) {
                throw new IllegalArgumentException(
                        "GremlinId cannot be null, please ensure " + field.getName() + " is populated.");
            }
        }
    }

    /**
     * Sets the partition key value from the field if it is marked with a GremlinPartitionKey annotation.
     * Will either use the partitionKeyFieldName defined by annotation, when defined, or will default to the field name.
     *
     * @param field   the field definition to extract the value using
     * @param results the GremlinVertex being updated
     * @param from    the instance of the class to extract the values from
     * @throws IllegalAccessException When the field is not accessible from this utility
     */
    private static void setPartitionKey(Field field, com.azure.graph.bulk.impl.model.GremlinVertex results, Object from)
            throws IllegalAccessException {
        var pkAnnotationClass = GremlinPartitionKey.class;
        if (field.isAnnotationPresent(pkAnnotationClass)) {
            var pkAnnotation = field.getAnnotation(pkAnnotationClass);

            var pk = new com.azure.graph.bulk.impl.model.GremlinPartitionKey(
                    pkAnnotation.fieldName().isBlank() ? field.getName() : pkAnnotation.fieldName(),
                    (String) field.get(from));

            results.setPartitionKey(pk);
        }
    }

    /**
     * Sets the value of the label if the field has been marked with a @GremlinLabel annotation.
     * Will override any value already set for the label by either the class annotation or the getter method
     *
     * @param field   the field definition to extract the value using
     * @param results the GremlinVertex being updated
     * @param from    the instance of the class to extract the values from
     * @throws IllegalAccessException When the field is not accessible from this utility
     */
    private static void setLabel(Field field, com.azure.graph.bulk.impl.model.GremlinVertex results, Object from) throws IllegalAccessException {
        if (!field.isAnnotationPresent(GremlinLabel.class)) return;

        results.setLabel((String) field.get(from));
    }

    /**
     * Sets the label value from the results of calling the method marked with a GremlinLabelGetter annotation
     *
     * @param clazz   the class of the object being used
     * @param results the GremlinVertex being updated
     * @param from    the instance of the class to extract the values from
     * @throws InvocationTargetException When the method executed throws an exceptions
     * @throws IllegalAccessException    Guards in place to prevent exception from being thrown
     */
    private static void setLabelFromGetter(Class<?> clazz, com.azure.graph.bulk.impl.model.GremlinVertex results, Object from)
            throws InvocationTargetException, IllegalAccessException {
        for (Method method : MethodUtils.getMethodsWithAnnotation(clazz, GremlinLabelGetter.class)) {
            if (!method.isAnnotationPresent(GremlinLabelGetter.class)) continue;
            var vertexLabel = (String) method.invoke(from);

            if (!vertexLabel.isBlank()) {
                results.setLabel(vertexLabel);
            }
        }
    }

    /**
     * Concatenates the properties from fields marked with the GremlinPropertyMap annotation to the target results
     * Properties bag
     *
     * @param field   the field definition to extract the value using
     * @param results the GremlinVertex being updated
     * @param from    the instance of the class to extract the values from
     * @throws IllegalAccessException When the field is not accessible from this utility
     */
    private static void setPropertyMap(Field field, com.azure.graph.bulk.impl.model.GremlinVertex results, Object from) throws IllegalAccessException {
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
     * @param results the GremlinVertex being updated
     * @param from    the instance of the class to extract the values from
     * @throws IllegalAccessException When the field is not accessible from this utility
     */
    private static void setPropertyValues(Field field, com.azure.graph.bulk.impl.model.GremlinVertex results, Object from)
            throws IllegalAccessException {
        if (field.isAnnotationPresent(GremlinIgnore.class) ||
                field.isAnnotationPresent(GremlinId.class) ||
                field.isAnnotationPresent(GremlinPartitionKey.class) ||
                field.isAnnotationPresent(GremlinLabel.class) ||
                field.isAnnotationPresent(GremlinPropertyMap.class)
        ) return;

        var value = field.get(from);
        if (value != null) {
            var key = field.getName();
            if (field.isAnnotationPresent(GremlinProperty.class)) {
                key = field.getAnnotation(GremlinProperty.class).name();
            }
            results.addProperty(key, value);
        }
    }
}
