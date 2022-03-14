// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a property that should be written to either an GremlinEdge or GremlinVertex,
 * by default all none null fields are written to the Edge and Vertex when there is not a
 * GremlinIgnore annotation on the field, this annotation allows for overriding the name
 * of the property in the Edge or Vertex.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface GremlinProperty {
    /**
     * Represents the name of the property on either the GremlinEdge or GremlinVertex. No need for the annotation
     * if this value matches the name of the property on the originating class.
     *
     * @return String value representing the name of the property
     */
    String name();
}
