// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.model.GremlinEdge;
import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import com.azure.graph.bulk.impl.model.GremlinPartitionKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
class GremlinEdgeSerializationTest {

    private ObjectMapper mapper;

    @BeforeAll
    void setup() {
        mapper = BulkGremlinObjectMapper.getBulkGremlinObjectMapper();
    }

    @Test
    void testSerializationOfBasicGremlinEdge() throws JsonProcessingException {
        var edge = getGremlinEdge();
        var serializedContent = mapper.writeValueAsString(edge);

        assertNotNull(serializedContent);
        assertTrue(serializedContent.contains("\"version\":\"2.0\""));
        assertTrue(serializedContent.contains("\"something\":\"awesome\""));
        assertTrue(serializedContent.contains("\"label\":\"schema\""));
    }

    private GremlinEdge getGremlinEdge() {
        GremlinEdge edge = GremlinEdge.builder()
                .id(UUID.randomUUID().toString())
                .destinationVertexInfo(new GremlinEdgeVertexInfo(
                        UUID.randomUUID().toString(),
                        "in",
                        "product"))
                .sourceVertexInfo(new GremlinEdgeVertexInfo(
                        UUID.randomUUID().toString(),
                        "out",
                        "product"))
                .label("schema")
                .partitionKey(new GremlinPartitionKey("classification", "product"))
                .properties(new HashMap<>())
                .build();

        edge.addProperty("something", "awesome");
        edge.addProperty("version", "2.0");

        return edge;
    }
}
