// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.model.GremlinPartitionKey;
import com.azure.graph.bulk.impl.model.GremlinVertex;
import com.azure.graph.bulk.sample.model.PersonVertex;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ObjectToVertexTest {

    @Test
    void PersonVertexToGremlinVertexTest() {
        PersonVertex source = getPersonVertex();
        GremlinVertex converted = ObjectToVertex.toGremlinVertex(source);

        assertEquals("PERSON", converted.getLabel());
        assertEquals(source.id, converted.getId());

        validatePartitionKey(source, converted);
        assertTrue(converted.getProperties().containsKey("ElectronicMail"));
        assertTrue(converted.getProperties().containsKey("firstName"));
        assertTrue(converted.getProperties().containsKey("lastName"));

        assertFalse(converted.getProperties().containsKey("isSpecial"));
        assertFalse(converted.getProperties().containsKey("email"));

    }

    private void validatePartitionKey(PersonVertex source, GremlinVertex converted) {
        GremlinPartitionKey partitionKey = converted.getPartitionKey();

        assertNotNull(partitionKey);
        assertEquals("country", partitionKey.getFieldName());
        assertEquals(source.country, partitionKey.getValue());
    }

    private PersonVertex getPersonVertex() {

        return PersonVertex.builder()
                .id(UUID.randomUUID().toString())
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .country("Neverland")
                .isSpecial(Boolean.FALSE).build();
    }
}
