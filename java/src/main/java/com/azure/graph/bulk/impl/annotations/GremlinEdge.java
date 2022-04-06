// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Identifies a class as a gremlin edge for the purpose of being able to convert into a GremlinEdge object.
 * Is required on objects that will be converted into a GremlinEdge.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface GremlinEdge {
    /**
     * Defines the label for the GremlinEdge object created. Defaults to an empty string.
     * Can be overridden by either a field annotated with the GremlinLabel annotation or
     * a method annotated with the GremlinLabelGetter annotation
     *
     * @return String representing the label
     */
    String label() default "";

    /**
     * Identifies the field name for the partitionKey. Should match the PartitionKey
     * path of the container the Edge interacts with.
     *
     * @return String representing the name of the field for the partitionKey
     */
    String partitionKeyFieldName();
}
