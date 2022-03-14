package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import com.azure.graph.bulk.sample.model.RelationshipEdge;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ObjectToEdgeTest {

    @SneakyThrows
    @Test
    void RelationshipEdgeToGremlinEdgeTest() {
        var edge = getRelationshipGraphEdge();
        var results = ObjectToEdge.toGremlinEdge(edge);

        assertEquals(edge.getDestinationVertexInfo().getId(), results.getDestinationVertexInfo().getId());
        assertEquals(edge.getSourceVertexInfo().getId(), results.getSourceVertexInfo().getId());
        assertEquals(edge.getDestinationVertexInfo().getPartitionKey(), results.getDestinationVertexInfo().getPartitionKey());
        assertEquals(edge.getSourceVertexInfo().getPartitionKey(), results.getSourceVertexInfo().getPartitionKey());
        assertEquals(edge.getDestinationVertexInfo().getLabel(), results.getDestinationVertexInfo().getLabel());
        assertEquals(edge.getSourceVertexInfo().getLabel(), results.getSourceVertexInfo().getLabel());

        assertNotNull(results.getId());

    }

    private RelationshipEdge getRelationshipGraphEdge() {
        var edge = new RelationshipEdge(
                new GremlinEdgeVertexInfo(
                        UUID.randomUUID().toString(),
                        "REL1",
                        "rel1"),
                new GremlinEdgeVertexInfo(
                        UUID.randomUUID().toString(),
                        "REL2",
                        "rel2"),
                "friend"
        );

        return edge;
    }
}
