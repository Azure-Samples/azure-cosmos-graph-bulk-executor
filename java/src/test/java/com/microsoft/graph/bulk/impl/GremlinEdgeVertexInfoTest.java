package com.microsoft.graph.bulk.impl;

import com.microsoft.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GremlinEdgeVertexInfoTest {

    @Test
    void TestEquityWithJustId() {
        var id = UUID.randomUUID().toString();

        var eviOne = new GremlinEdgeVertexInfo(id);
        var eviTwo = new GremlinEdgeVertexInfo(id);

        assertEquals(eviOne, eviTwo);
        assertEquals(eviOne.hashCode(), eviTwo.hashCode());
    }
}
