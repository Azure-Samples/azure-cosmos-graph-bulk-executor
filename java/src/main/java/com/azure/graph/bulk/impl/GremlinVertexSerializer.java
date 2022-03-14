// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.model.GremlinPartitionKey;
import com.azure.graph.bulk.impl.model.GremlinVertex;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Serializer capable to serializing a GremlinVertex in the structure required by the CosmosDb Graph database
 */
public class GremlinVertexSerializer extends StdSerializer<GremlinVertex> {
    protected GremlinVertexSerializer(Class<GremlinVertex> t) {
        super(t);
    }

    /**
     * Writes the contents of the GremlinVertex to the jsonGenerator
     * provided in the structure required by a CosmosDb Graph database
     *
     * @param gremlinVertex      Vertex to generate the json document from
     * @param jsonGenerator      Generator to write the Edge to
     * @param serializerProvider Serialization provider
     * @throws IOException When there is a IO failure writing to the jsonGenerator
     */
    @Override
    public void serialize(GremlinVertex gremlinVertex,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(GremlinFieldNames.VERTEX_ID, gremlinVertex.getId());

        jsonGenerator.writeStringField(GremlinFieldNames.VERTEX_LABEL, gremlinVertex.getLabel());

        GremlinPartitionKey partitionKey = gremlinVertex.getPartitionKey();

        jsonGenerator.writeStringField(partitionKey.getFieldName(), partitionKey.getValue());

        gremlinVertex.getProperties().forEach((key, value) -> {
            if (value != null) {
                try {
                    jsonGenerator.writeArrayFieldStart(key);
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField(GremlinFieldNames.PROPERTY_ID, value.getId());
                    jsonGenerator.writeObjectField(GremlinFieldNames.PROPERTY_VALUE, value.getValue());
                    jsonGenerator.writeEndObject();
                    jsonGenerator.writeEndArray();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        });

        jsonGenerator.writeEndObject();
    }
}
