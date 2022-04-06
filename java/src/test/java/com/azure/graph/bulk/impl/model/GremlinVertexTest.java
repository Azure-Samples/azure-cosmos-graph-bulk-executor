// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GremlinVertexTest {

    @Test
    void GoodVertexPassesValidation() {
        GremlinVertex vertex = GremlinVertex.builder()
                .id("Good")
                .label("Vertex")
                .partitionKey(GremlinPartitionKey.builder().fieldName("Field").value("Value").build())
                .build();

        assertDoesNotThrow(vertex::validate);
    }

    @Test
    void EmptyIdThrowsException() {
        GremlinVertex vertex = GremlinVertex.builder()
                .id("")
                .label("Vertex")
                .partitionKey(GremlinPartitionKey.builder().fieldName("Field").value("Value").build())
                .build();

        assertThrows(IllegalStateException.class, vertex::validate);
    }

    @Test
    void NullIdThrowsException() {
        GremlinVertex vertex = GremlinVertex.builder()
                .label("Vertex")
                .partitionKey(GremlinPartitionKey.builder().fieldName("Field").value("Value").build())
                .build();

        assertThrows(IllegalStateException.class, vertex::validate);
    }

    @Test
    void EmptyLabelThrowsException() {
        GremlinVertex vertex = GremlinVertex.builder()
                .id("Bad")
                .label("")
                .partitionKey(GremlinPartitionKey.builder().fieldName("Field").value("Value").build())
                .build();

        assertThrows(IllegalStateException.class, vertex::validate);
    }

    @Test
    void NullLabelThrowsException() {
        GremlinVertex vertex = GremlinVertex.builder()
                .id("Bad")
                .partitionKey(GremlinPartitionKey.builder().fieldName("Field").value("Value").build())
                .build();

        assertThrows(IllegalStateException.class, vertex::validate);
    }

    @Test
    void NullPartitionKeyThrowsException() {
        GremlinVertex vertex = GremlinVertex.builder()
                .id("Bad")
                .label("Vertex")
                .build();

        assertThrows(IllegalStateException.class, vertex::validate);
    }
}
