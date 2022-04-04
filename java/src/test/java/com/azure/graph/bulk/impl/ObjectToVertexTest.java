// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.annotations.GremlinId;
import com.azure.graph.bulk.impl.annotations.GremlinLabel;
import com.azure.graph.bulk.impl.annotations.GremlinLabelGetter;
import com.azure.graph.bulk.impl.model.AnnotationValidationException;
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

    @com.azure.graph.bulk.impl.annotations.GremlinVertex(label = "TheLabel")
    static class SoManyProblems {
        @GremlinId
        public String id;
        @GremlinId
        public String id2;
        @com.azure.graph.bulk.impl.annotations.GremlinPartitionKey
        public String partitionKey;
        @com.azure.graph.bulk.impl.annotations.GremlinPartitionKey
        public String partitionKey2;
        @GremlinLabel
        public String label;

        @GremlinLabelGetter
        public String label() {
            return "TheLabel";
        }
    }

    @Test
    void SoManyProblemsThrowsException() {
        SoManyProblems problems = new SoManyProblems();
        assertThrows(AnnotationValidationException.class, () -> ObjectToVertex.toGremlinVertex(problems));
    }
}
