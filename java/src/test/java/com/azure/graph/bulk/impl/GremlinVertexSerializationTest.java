package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.model.GremlinVertex;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
class GremlinVertexSerializationTest {
    private ObjectMapper mapper;

    @BeforeAll
    void setup() {
        mapper = BulkGremlinObjectMapper.getBulkGremlinObjectMapper();
    }

    @Test
    void testSerializationOfBasicGremlinVertex() throws JsonProcessingException {
        var vertex = getGremlinVertex();
        var serializedContent = mapper.writeValueAsString(vertex);

        assertNotNull(serializedContent);
        assertTrue(serializedContent.contains("\"_value\":\"1.0\""));
        assertTrue(serializedContent.contains("\"version\":[{"));
        assertTrue(serializedContent.contains("\"classification\":\"product\","));
        assertTrue(serializedContent.contains("\"label\":\"schema\","));
    }

    private GremlinVertex getGremlinVertex() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());

        GremlinVertex vertex = GremlinVertex.builder()
                .id(UUID.randomUUID().toString())
                .label("schema")
                .properties(new HashMap<>())
                .build();

        vertex.addProperty("classification", "product", true);
        vertex.addProperty("version", "1.0");
        vertex.addProperty("description", "The root schema");
        vertex.addProperty("lastModifiedDate", formatter.format(date));

        return vertex;
    }
}
