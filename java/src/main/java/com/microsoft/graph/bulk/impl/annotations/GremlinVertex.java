package com.microsoft.graph.bulk.impl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a class as a gremlin vertex for the purpose of being able to convert into a GremlinVertex object.
 * Is required on classes that will be converted into a GremlinVertex.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface GremlinVertex {
    /**
     * Defines the label for the GremlinVertex object created. Defaults to an empty string.
     * Can be overridden by either a field annotated with the GremlinLabel annotation or
     * a method annotated with the GremlinLabelGetter annotation
     *
     * @return String representing the label
     */
    String label() default "";
}

