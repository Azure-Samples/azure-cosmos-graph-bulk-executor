// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.model.GremlinEdge;
import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import com.azure.graph.bulk.impl.model.GremlinPartitionKey;
import com.azure.graph.bulk.sample.model.RelationshipEdge;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ObjectToEdgeTest {

    @Test
    void RelationshipEdgeToGremlinEdgeTest() {
        RelationshipEdge edge = getRelationshipGraphEdge();
        GremlinEdge results = ObjectToEdge.toGremlinEdge(edge);

        assertEquals(edge.destinationVertexInfo.getId(), results.getDestinationVertexInfo().getId());
        assertEquals(edge.sourceVertexInfo.getId(), results.getSourceVertexInfo().getId());
        assertEquals(edge.destinationVertexInfo.getPartitionKey(),
                results.getDestinationVertexInfo().getPartitionKey());
        assertEquals(edge.sourceVertexInfo.getPartitionKey(), results.getSourceVertexInfo().getPartitionKey());
        assertEquals(edge.destinationVertexInfo.getLabel(), results.getDestinationVertexInfo().getLabel());
        assertEquals(edge.sourceVertexInfo.getLabel(), results.getSourceVertexInfo().getLabel());

        assertNotNull(results.getId());

    }

    private RelationshipEdge getRelationshipGraphEdge() {
        RelationshipEdge edge = RelationshipEdge.builder()
                .sourceVertexInfo(GremlinEdgeVertexInfo.builder()
                        .id(UUID.randomUUID().toString())
                        .label("REL1")
                        .partitionKey(GremlinPartitionKey.builder().value("rel1").build()).build())
                .destinationVertexInfo(GremlinEdgeVertexInfo.builder()
                        .id(UUID.randomUUID().toString())
                        .label("REL2")
                        .partitionKey(GremlinPartitionKey.builder().value("rel2").build()).build())
                .relationshipType("friend")
                .build();

        return edge;
    }
}
