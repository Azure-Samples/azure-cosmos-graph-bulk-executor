// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.model.GremlinEdge;
import com.azure.graph.bulk.impl.model.GremlinVertex;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class BulkGremlinObjectMapper {
    private BulkGremlinObjectMapper() {
        throw new IllegalStateException("Utility class, should not be constructed");
    }

    /**
     * Defines an ObjectMapper that has the Serialization Modules for both GremlinVertex and GremlinEdge classes
     */
    public static ObjectMapper getBulkGremlinObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule vertexModule = new SimpleModule("GremlinVertexModule");
        vertexModule.addSerializer(GremlinVertex.class, new GremlinVertexSerializer(GremlinVertex.class));
        mapper.registerModule(vertexModule);

        SimpleModule edgeModule = new SimpleModule("GremlinEdgeModule");
        edgeModule.addSerializer(GremlinEdge.class, new GremlinEdgeSerializer(GremlinEdge.class));
        mapper.registerModule(edgeModule);

        return mapper;
    }
}
