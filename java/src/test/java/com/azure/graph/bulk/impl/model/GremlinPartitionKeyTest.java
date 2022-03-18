package com.azure.graph.bulk.impl.model;

import com.azure.graph.bulk.sample.model.PersonVertex;
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
        GremlinPartitionKey.GremlinPartitionKeyBuilder keyBuilder =
                GremlinPartitionKey.builder().fieldName("good");

        assertThrows(IllegalStateException.class, keyBuilder::build);
    }

    @Test
    void AcceptsBooleanValue() {
        GremlinPartitionKey.GremlinPartitionKeyBuilder keyBuilder =
                GremlinPartitionKey.builder().fieldName("good").value(false);

        assertDoesNotThrow(keyBuilder::build);
    }

    @Test
    void AcceptsIntegerValue() {
        GremlinPartitionKey.GremlinPartitionKeyBuilder keyBuilder =
                GremlinPartitionKey.builder().fieldName("good").value(1);

        assertDoesNotThrow(keyBuilder::build);
    }

    @Test
    void AcceptsFloatValue() {
        GremlinPartitionKey.GremlinPartitionKeyBuilder keyBuilder =
                GremlinPartitionKey.builder().fieldName("good").value(1.1f);

        assertDoesNotThrow(keyBuilder::build);
    }

    @Test
    void AcceptsDoubleValue() {
        GremlinPartitionKey.GremlinPartitionKeyBuilder keyBuilder =
                GremlinPartitionKey.builder().fieldName("good").value(1.1d);

        assertDoesNotThrow(keyBuilder::build);
    }

    @Test
    void AcceptsByteValue() {
        GremlinPartitionKey.GremlinPartitionKeyBuilder keyBuilder =
                GremlinPartitionKey.builder().fieldName("good").value((byte) 10);

        assertDoesNotThrow(keyBuilder::build);
    }

    @Test
    void AcceptsLongValue() {
        GremlinPartitionKey.GremlinPartitionKeyBuilder keyBuilder =
                GremlinPartitionKey.builder().fieldName("good").value(10L);

        assertDoesNotThrow(keyBuilder::build);
    }

    @Test
    void AcceptsCharValue() {
        GremlinPartitionKey.GremlinPartitionKeyBuilder keyBuilder =
                GremlinPartitionKey.builder().fieldName("good").value('L');

        assertDoesNotThrow(keyBuilder::build);
    }

    @Test
    void AcceptsShortValue() {
        GremlinPartitionKey.GremlinPartitionKeyBuilder keyBuilder =
                GremlinPartitionKey.builder().fieldName("good").value((short) 1);

        assertDoesNotThrow(keyBuilder::build);
    }

    @Test
    void ThrowsExceptionWithNonPrimitiveDataType() {
        GremlinPartitionKey.GremlinPartitionKeyBuilder keyBuilder =
                GremlinPartitionKey.builder().fieldName("bad").value(PersonVertex.builder().build());

        assertThrows(IllegalStateException.class, keyBuilder::build);
    }
}
