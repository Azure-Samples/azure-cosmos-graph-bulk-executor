// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a field that is the Partition Key of either an Edge or Vertex
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface GremlinPartitionKey {
    /**
     * Can be used to define the name of the field on the GremlinVertex or GremlinEdge for the partitionKey.
     * Ensure this matches the path for the partitionKey in the Container
     *
     * @return String representing the field name of the partitionKey
     */
    String fieldName() default "";
}
