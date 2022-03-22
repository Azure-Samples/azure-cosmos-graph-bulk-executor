// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.model;

import java.util.Map;

public class GremlinVertex {
    private String id;
    private String label;
    private GremlinPartitionKey partitionKey;
    private Map<String, Object> properties;

    public void addProperty(String key, Object value) {
        addProperty(key, value, false);
    }

    public void addProperty(String key, Object value, boolean isPartitionKey) {
        if (value == null) return;

        if (isPartitionKey) {
            partitionKey = GremlinPartitionKey.builder()
                    .fieldName(key)
                    .value(value.toString())
                    .build();
        } else {
            properties.put(key, value);
        }
    }

    public void validate() {
        if (id == null || id.isBlank()) throw new IllegalStateException("Missing ID on GremlinVertex");

        if (label == null || label.isBlank()) throw new IllegalStateException(
                String.format("Missing label on GremlinVertex: %s", id));

        if (partitionKey == null) throw new IllegalStateException(
                String.format("Missing Partition Key on GremlinVertex %s", this.id));
        partitionKey.validate();
    }

    GremlinVertex(GremlinVertexBuilder builder) {
        this.id = builder.id;
        this.label = builder.label;
        this.partitionKey = builder.partitionKey;
        this.properties = builder.properties;
    }

    public static GremlinVertex.GremlinVertexBuilder builder() {
        return new GremlinVertex.GremlinVertexBuilder();
    }

    public String getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public GremlinPartitionKey getPartitionKey() {
        return this.partitionKey;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPartitionKey(GremlinPartitionKey partitionKey) {
        this.partitionKey = partitionKey;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GremlinVertex)) return false;

        GremlinVertex other = (GremlinVertex) o;

        if (isNotEqual(id, other.id)) return false;
        if (isNotEqual(label, other.label)) return false;
        if (isNotEqual(partitionKey, other.partitionKey)) return false;
        //noinspection RedundantIfStatement
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
        result = result * 59 + (partitionKey == null ? 43 : partitionKey.hashCode());
        result = result * 59 + (properties == null ? 43 : properties.hashCode());
        return result;
    }

    public static class GremlinVertexBuilder {
        private String id;
        private String label;
        private GremlinPartitionKey partitionKey;
        private Map<String, Object> properties;

        GremlinVertexBuilder() {
        }

        public GremlinVertex.GremlinVertexBuilder id(String id) {
            this.id = id;
            return this;
        }

        public GremlinVertex.GremlinVertexBuilder label(String label) {
            this.label = label;
            return this;
        }

        public GremlinVertex.GremlinVertexBuilder partitionKey(GremlinPartitionKey partitionKey) {
            this.partitionKey = partitionKey;
            return this;
        }

        public GremlinVertex.GremlinVertexBuilder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public GremlinVertex build() {
            return new GremlinVertex(this);
        }
    }
}
