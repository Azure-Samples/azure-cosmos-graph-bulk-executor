// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.model;

public class GremlinPartitionKey {
    private final String fieldName;
    private String value;

    public void validate() {
        if (fieldName == null || fieldName.isBlank())
            throw new IllegalStateException("Field name for partition key is missing");
        if (value == null || value.isBlank()) throw new IllegalStateException("Partition key value is missing");
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static GremlinPartitionKey.GremlinPartitionKeyBuilder builder() {
        return new GremlinPartitionKey.GremlinPartitionKeyBuilder();
    }

    public GremlinPartitionKey(GremlinPartitionKeyBuilder builder) {
        this.fieldName = builder.fieldName;
        this.value = builder.value;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GremlinPartitionKey)) return false;

        GremlinPartitionKey other = (GremlinPartitionKey) o;

        if (isNotEqual(fieldName, other.fieldName)) return false;
        if (isNotEqual(value, other.value)) return false;

        return true;
    }

    private boolean isNotEqual(Object source, Object other) {
        if (source == null && other == null) return false;
        if (source == null) return true;
        return !source.equals(other);
    }

    public static class GremlinPartitionKeyBuilder {
        private String fieldName;
        private String value;

        GremlinPartitionKeyBuilder() {
        }

        public GremlinPartitionKey.GremlinPartitionKeyBuilder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public GremlinPartitionKey.GremlinPartitionKeyBuilder value(String value) {
            this.value = value;
            return this;
        }

        public GremlinPartitionKey build() {
            return new GremlinPartitionKey(this);
        }
    }

    public int hashCode() {
        int result = 59 + (fieldName == null ? 43 : fieldName.hashCode());
        result = result * 59 + (value == null ? 43 : value.hashCode());
        return result;
    }
}
