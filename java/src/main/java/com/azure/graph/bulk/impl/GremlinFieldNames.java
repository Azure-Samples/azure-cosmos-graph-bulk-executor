// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

/**
 * Defines the names of the fields on the Edge and Vertex documents sent to the CosmosDb Graph Database
 */
public final class GremlinFieldNames {
    private GremlinFieldNames() {
        throw new IllegalStateException("Utility class, should not be constructed");
    }

    public static final String VERTEX_ID = "id";
    public static final String VERTEX_LABEL = "label";

    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_VALUE = "_value";

    public static final String EDGE_LABEL = "label";
    public static final String EDGE_ID = "id";
    public static final String EDGE_DESTINATIONV_ID = "_sink";
    public static final String EDGE_DESTINATIONV_LABEL = "_sinkLabel";
    public static final String EDGE_DESTINATIONV_PARTITION = "_sinkPartition";

    public static final String EDGE_SOURCEV_ID = "_vertexId";
    public static final String EDGE_SOURCEV_LABEL = "_vertexLabel";
    public static final String EDGE_IDENTICATOR = "_isEdge";
}
