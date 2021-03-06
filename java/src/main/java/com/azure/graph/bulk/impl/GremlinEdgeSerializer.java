// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.graph.bulk.impl.model.GremlinEdge;
import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Serializer capable to serializing a GremlinEdge in the structure required by the CosmosDb Graph database
 */
public class GremlinEdgeSerializer extends StdSerializer<GremlinEdge> {
    protected GremlinEdgeSerializer(Class<GremlinEdge> t) {
        super(t);
    }

    /**
     * Writes the contents of the GremlinEdge to the jsonGenerator
     * provided in the structure required by a CosmosDb Graph database
     *
     * @param gremlinEdge        Edge to generate the json document from
     * @param jsonGenerator      Generator to write the Edge to
     * @param serializerProvider Serialization provider
     * @throws IOException When there is a IO failure writing to the jsonGenerator
     */
    @Override
    public void serialize(GremlinEdge gremlinEdge, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeBooleanField(GremlinFieldNames.EDGE_IDENTICATOR, true);

        jsonGenerator.writeStringField(GremlinFieldNames.EDGE_ID, gremlinEdge.getId());
        jsonGenerator.writeStringField(GremlinFieldNames.EDGE_LABEL, gremlinEdge.getLabel());

        GremlinEdgeVertexInfo sourceVertexInfo = gremlinEdge.getSourceVertexInfo();
        GremlinEdgeVertexInfo destinationVertexInfo = gremlinEdge.getDestinationVertexInfo();

        jsonGenerator.writeObjectField(gremlinEdge.getPartitionKey().getFieldName(),
                gremlinEdge.getPartitionKey().getValue());
        jsonGenerator.writeObjectField(GremlinFieldNames.EDGE_DESTINATIONV_PARTITION,
                destinationVertexInfo.getPartitionKey().getValue());

        jsonGenerator.writeStringField(GremlinFieldNames.EDGE_DESTINATIONV_ID, destinationVertexInfo.getId());
        jsonGenerator.writeStringField(GremlinFieldNames.EDGE_DESTINATIONV_LABEL, destinationVertexInfo.getLabel());

        jsonGenerator.writeStringField(GremlinFieldNames.EDGE_SOURCEV_ID, sourceVertexInfo.getId());
        jsonGenerator.writeStringField(GremlinFieldNames.EDGE_SOURCEV_LABEL, sourceVertexInfo.getLabel());

        gremlinEdge.getProperties().forEach((key, value) -> {
            if (value != null) {
                try {
                    jsonGenerator.writeObjectField(key, value);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        });

        jsonGenerator.writeEndObject();
    }
}
