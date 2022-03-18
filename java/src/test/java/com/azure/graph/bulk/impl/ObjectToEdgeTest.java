// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.model.GremlinEdge;
import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import com.azure.graph.bulk.sample.model.RelationshipEdge;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ObjectToEdgeTest {

    @Test
    void RelationshipEdgeToGremlinEdgeTest() {
        RelationshipEdge edge = getRelationshipGraphEdge();
        GremlinEdge results = ObjectToEdge.toGremlinEdge(edge);

        assertEquals(edge.getDestinationVertexInfo().getId(), results.getDestinationVertexInfo().getId());
        assertEquals(edge.getSourceVertexInfo().getId(), results.getSourceVertexInfo().getId());
        assertEquals(edge.getDestinationVertexInfo().getPartitionKey(),
                results.getDestinationVertexInfo().getPartitionKey());
        assertEquals(edge.getSourceVertexInfo().getPartitionKey(), results.getSourceVertexInfo().getPartitionKey());
        assertEquals(edge.getDestinationVertexInfo().getLabel(), results.getDestinationVertexInfo().getLabel());
        assertEquals(edge.getSourceVertexInfo().getLabel(), results.getSourceVertexInfo().getLabel());

        assertNotNull(results.getId());

    }

    private RelationshipEdge getRelationshipGraphEdge() {
        RelationshipEdge edge = new RelationshipEdge(
                GremlinEdgeVertexInfo.builder()
                        .id(UUID.randomUUID().toString())
                        .label("REL1")
                        .partitionKey("rel1").build(),
                GremlinEdgeVertexInfo.builder()
                        .id(UUID.randomUUID().toString())
                        .label("REL2")
                        .partitionKey("rel2").build(),
                "friend"
        );

        return edge;
    }
}
