// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a field as one of the two Vertices on the Edge
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface GremlinEdgeVertex {
    enum Direction {
        SOURCE, DESTINATION
    }

    /**
     * Defines which side of the edge the vertex represents, the Source Vertex or the Destination Vertex
     *
     * @return Value indicating if the vertex is either source or destination
     */
    Direction direction();
}
