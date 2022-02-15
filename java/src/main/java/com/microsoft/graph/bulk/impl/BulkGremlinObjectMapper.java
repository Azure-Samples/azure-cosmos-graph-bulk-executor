package com.microsoft.graph.bulk.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.microsoft.graph.bulk.impl.model.GremlinEdge;
import com.microsoft.graph.bulk.impl.model.GremlinVertex;

public final class BulkGremlinObjectMapper {
    private BulkGremlinObjectMapper() {
        throw new IllegalStateException("Utility class, should not be constructed");
    }

    /**
     * Defines an ObjectMapper that has the Serialization Modules for both GremlinVertex and GremlinEdge classes
     */
    public static ObjectMapper getBulkGremlinObjectMapper() {
        var mapper = new ObjectMapper();

        var vertexModule = new SimpleModule("GremlinVertexModule");
        vertexModule.addSerializer(GremlinVertex.class, new GremlinVertexSerializer(GremlinVertex.class));
        mapper.registerModule(vertexModule);

        var edgeModule = new SimpleModule("GremlinEdgeModule");
        edgeModule.addSerializer(GremlinEdge.class, new GremlinEdgeSerializer(GremlinEdge.class));
        mapper.registerModule(edgeModule);

        return mapper;
    }
}
