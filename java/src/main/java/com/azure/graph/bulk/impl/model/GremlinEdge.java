// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class GremlinEdge {
    private String id;
    private String label;

    private GremlinEdgeVertexInfo destinationVertexInfo;

    private GremlinEdgeVertexInfo sourceVertexInfo;

    private GremlinPartitionKey partitionKey;

    private HashMap<String, Object> properties;

    public GremlinEdge() {
        id = UUID.randomUUID().toString();
        destinationVertexInfo = new GremlinEdgeVertexInfo();
        sourceVertexInfo = new GremlinEdgeVertexInfo();
        properties = new HashMap<>();
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public void putProperties(Map<String, Object> properties) {
        this.properties.putAll(properties);
    }

    public void validate() {
        if (id == null || id.isBlank()) throw new IllegalStateException("Missing ID on GremlinEdge");

        if (label == null || label.isBlank()) throw new IllegalStateException(
                String.format("Missing label on GremlinEdge: %s", id));

        if (this.sourceVertexInfo == null) throw new IllegalStateException(
                String.format("Missing source vertex information on GremlinEdge: %s", id));

        if (this.destinationVertexInfo == null) throw new IllegalStateException(
                String.format("Missing destination vertex information on GremlinEdge: %s", id));

        if (partitionKey == null) throw new IllegalStateException(
                String.format("Missing Partition Key on GremlinEdge ID: %s, Source ID: %s, Destination ID: %s",
                        this.id, this.sourceVertexInfo.getId(), this.getDestinationVertexInfo().getId()));
        partitionKey.validate();
    }
}
