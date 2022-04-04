// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import com.azure.graph.bulk.impl.model.GremlinPartitionKey;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GremlinEdgeVertexInfoTest {

    @Test
    void TestEquityWithJustId() {
        String id = UUID.randomUUID().toString();

        GremlinEdgeVertexInfo eviOne = GremlinEdgeVertexInfo.builder().id(id).build();
        GremlinEdgeVertexInfo eviTwo = GremlinEdgeVertexInfo.builder().id(id).build();

        assertEquals(eviOne, eviTwo);
        assertEquals(eviOne.hashCode(), eviTwo.hashCode());
    }

    @Test
    void GoodInfoPassesValidation() {
        GremlinEdgeVertexInfo info = GremlinEdgeVertexInfo.builder()
                .id("MyId")
                .label("MyLabel")
                .partitionKey(GremlinPartitionKey.builder().fieldName("Field").value("Value").build())
                .build();

        assertDoesNotThrow(info::validate);
    }

    @Test
    void MissingIdFailsValidation() {
        GremlinEdgeVertexInfo info = GremlinEdgeVertexInfo.builder()
                .label("MyLabel")
                .partitionKey(GremlinPartitionKey.builder().fieldName("Field").value("Value").build())
                .build();

        assertThrows(IllegalStateException.class, info::validate);
    }

    @Test
    void MissingLabelFailsValidation() {
        GremlinEdgeVertexInfo info = GremlinEdgeVertexInfo.builder()
                .id("MyId")
                .partitionKey(GremlinPartitionKey.builder().fieldName("Field").value("Value").build())
                .build();

        assertThrows(IllegalStateException.class, info::validate);
    }

    @Test
    void MissingPartitionKeyFailsValidation() {
        GremlinEdgeVertexInfo info = GremlinEdgeVertexInfo.builder()
                .id("MyId")
                .label("MyLabel")
                .build();

        assertThrows(IllegalStateException.class, info::validate);
    }
}
