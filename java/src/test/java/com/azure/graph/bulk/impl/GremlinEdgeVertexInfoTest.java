// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GremlinEdgeVertexInfoTest {

    @Test
    void TestEquityWithJustId() {
        String id = UUID.randomUUID().toString();

        GremlinEdgeVertexInfo eviOne = new GremlinEdgeVertexInfo(id);
        GremlinEdgeVertexInfo eviTwo = new GremlinEdgeVertexInfo(id);

        assertEquals(eviOne, eviTwo);
        assertEquals(eviOne.hashCode(), eviTwo.hashCode());
    }
}
