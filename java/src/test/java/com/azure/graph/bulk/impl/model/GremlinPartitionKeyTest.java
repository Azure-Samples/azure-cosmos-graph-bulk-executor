package com.azure.graph.bulk.impl.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GremlinPartitionKeyTest {
    @Test
    void GoodPartitionKeyPassesValidation() {
        GremlinPartitionKey key = GremlinPartitionKey.builder().fieldName("good").value("key").build();

        assertDoesNotThrow(key::validate);
    }

    @Test
    void NullFieldNameThrowsException() {
        GremlinPartitionKey key = GremlinPartitionKey.builder().fieldName(null).value("key").build();

        assertThrows(IllegalStateException.class, key::validate);
    }

    @Test
    void EmptyFieldNameThrowsException() {
        GremlinPartitionKey key = GremlinPartitionKey.builder().fieldName("").value("key").build();

        assertThrows(IllegalStateException.class, key::validate);
    }

    @Test
    void NullValueThrowsException() {
        GremlinPartitionKey key = GremlinPartitionKey.builder().fieldName("Bad").value(null).build();

        assertThrows(IllegalStateException.class, key::validate);
    }

    @Test
    void EmptyValueThrowsException() {
        GremlinPartitionKey key = GremlinPartitionKey.builder().fieldName("Bad").value("").build();

        assertThrows(IllegalStateException.class, key::validate);
    }
}
