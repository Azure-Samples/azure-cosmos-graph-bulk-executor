package com.microsoft.graph.bulk.impl;

/**
 * Defines the names of the fields on the Edge and Vertex documents sent to the CosmsoDb Graph Database
 */
public final class GremlinFieldNames {
    public static final String VERTEX_ID = "id";
    public static final String VERTEX_LABEL = "label";

    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_VALUE = "_value";

    public static final String EDGE_LABEL = "label";
    public static final String EDGE_ID = "id";
    public static final String EDGE_DESTINATIONV = "_sink";
    public static final String EDGE_DESTINATIONV_LABEL = "_sinkLabel";
    public static final String EDGE_DESTINATIONV_PARTITION = "_sinkPartition";

    public static final String EDGE_SOURCEV_ID = "_vertexId";
    public static final String EDGE_SOURCEV_LABEL = "_vertexLabel";
    public static final String EDGE_IDENTICATOR = "_isEdge";
}