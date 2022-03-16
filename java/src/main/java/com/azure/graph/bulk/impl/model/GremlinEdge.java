// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GremlinEdge {
    private String id;
    private String label;
    private GremlinEdgeVertexInfo destinationVertexInfo;
    private GremlinEdgeVertexInfo sourceVertexInfo;
    private GremlinPartitionKey partitionKey;
    private Map<String, Object> properties;

    public GremlinEdge() {
        id = UUID.randomUUID().toString();
        destinationVertexInfo = new GremlinEdgeVertexInfo();
        sourceVertexInfo = new GremlinEdgeVertexInfo();
        properties = Collections.unmodifiableMap(new HashMap<>());
    }

    public String getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDestinationVertexInfo(GremlinEdgeVertexInfo destinationVertexInfo) {
        this.destinationVertexInfo = destinationVertexInfo;
    }

    public void setSourceVertexInfo(GremlinEdgeVertexInfo sourceVertexInfo) {
        this.sourceVertexInfo = sourceVertexInfo;
    }

    public void setPartitionKey(GremlinPartitionKey partitionKey) {
        this.partitionKey = partitionKey;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public GremlinEdgeVertexInfo getDestinationVertexInfo() {
        return this.destinationVertexInfo;
    }

    public GremlinEdgeVertexInfo getSourceVertexInfo() {
        return this.sourceVertexInfo;
    }

    public GremlinPartitionKey getPartitionKey() {
        return this.partitionKey;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
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

    public static GremlinEdge.GremlinEdgeBuilder builder() {
        return new GremlinEdge.GremlinEdgeBuilder();
    }

    public GremlinEdge(GremlinEdgeBuilder builder) {
        this.id = builder.id;
        this.label = builder.label;
        this.destinationVertexInfo = builder.destinationVertexInfo;
        this.sourceVertexInfo = builder.sourceVertexInfo;
        this.partitionKey = builder.partitionKey;
        this.properties = builder.properties;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GremlinEdge))
            return false;

        GremlinEdge other = (GremlinEdge) o;

        if (isNotEqual(id, other.id)) return false;
        if (isNotEqual(label, other.label)) return false;
        if (isNotEqual(destinationVertexInfo, other.destinationVertexInfo)) return false;
        if (isNotEqual(sourceVertexInfo, other.sourceVertexInfo)) return false;
        if (isNotEqual(partitionKey, other.partitionKey)) return false;
        if (isNotEqual(properties, other.properties)) return false;

        return true;
    }

    private boolean isNotEqual(Object source, Object other) {
        if (source == null && other == null) return false;
        if (source == null) return true;
        return !source.equals(other);
    }

    public int hashCode() {
        int result = 59 + (id == null ? 43 : id.hashCode());
        result = result * 59 + (label == null ? 43 : label.hashCode());
        result = result * 59 + (destinationVertexInfo == null ? 43 : destinationVertexInfo.hashCode());
        result = result * 59 + (sourceVertexInfo == null ? 43 : sourceVertexInfo.hashCode());
        result = result * 59 + (partitionKey == null ? 43 : partitionKey.hashCode());
        result = result * 59 + (properties == null ? 43 : properties.hashCode());
        return result;
    }

    public static class GremlinEdgeBuilder {
        private String id;
        private String label;
        private GremlinEdgeVertexInfo destinationVertexInfo;
        private GremlinEdgeVertexInfo sourceVertexInfo;
        private GremlinPartitionKey partitionKey;
        private Map<String, Object> properties;

        GremlinEdgeBuilder() {
        }

        public GremlinEdge.GremlinEdgeBuilder id(String id) {
            this.id = id;
            return this;
        }

        public GremlinEdge.GremlinEdgeBuilder label(String label) {
            this.label = label;
            return this;
        }

        public GremlinEdge.GremlinEdgeBuilder destinationVertexInfo(GremlinEdgeVertexInfo destinationVertexInfo) {
            this.destinationVertexInfo = destinationVertexInfo;
            return this;
        }

        public GremlinEdge.GremlinEdgeBuilder sourceVertexInfo(GremlinEdgeVertexInfo sourceVertexInfo) {
            this.sourceVertexInfo = sourceVertexInfo;
            return this;
        }

        public GremlinEdge.GremlinEdgeBuilder partitionKey(GremlinPartitionKey partitionKey) {
            this.partitionKey = partitionKey;
            return this;
        }

        public GremlinEdge.GremlinEdgeBuilder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public GremlinEdge build() {
            return new GremlinEdge(this);
        }
    }
}
