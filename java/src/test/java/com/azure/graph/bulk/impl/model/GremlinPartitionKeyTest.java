package com.azure.graph.bulk.impl.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GremlinPartitionKeyTest {
    @Test
    void GoodPartitionKeyPassesValidation() {
        GremlinPartitionKey key = new GremlinPartitionKey("good", "key");

        assertDoesNotThrow(key::validate);
    }

    @Test
    void NullFieldNameThrowsException() {
        GremlinPartitionKey key = new GremlinPartitionKey(null, "key");

        assertThrows(IllegalStateException.class, key::validate);
    }

    @Test
    void EmptyFieldNameThrowsException() {
        GremlinPartitionKey key = new GremlinPartitionKey("", "key");

        assertThrows(IllegalStateException.class, key::validate);
    }

    @Test
    void NullValueThrowsException() {
        GremlinPartitionKey key = new GremlinPartitionKey("Bad", null);

        assertThrows(IllegalStateException.class, key::validate);
    }

    @Test
    void EmptyValueThrowsException() {
        GremlinPartitionKey key = new GremlinPartitionKey("Bad", "");

        assertThrows(IllegalStateException.class, key::validate);
    }
}
