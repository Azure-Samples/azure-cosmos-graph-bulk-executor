// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.model;

import com.azure.cosmos.implementation.Undefined;

public class GremlinPartitionKey {
    private final String fieldName;
    private Object value;

    public void validate() {
        if (fieldName == null || fieldName.isBlank())
            throw new IllegalStateException("Field name for partition key is missing");
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        if (value == null)
            throw new IllegalStateException("Partition key cannot be set to null value");
        if (!isValidDataType(value))
            throw new IllegalStateException("Primary key must be a primitive data type");
        this.value = value;
    }

    private boolean isValidDataType(Object value) {
        if (value instanceof Boolean) return true;
        if (value instanceof String) return true;
        if (value instanceof Integer) return true;
        if (value instanceof Short) return true;
        if (value instanceof Float) return true;
        if (value instanceof Character) return true;
        if (value instanceof Double) return true;
        if (value instanceof Byte) return true;
        //noinspection RedundantIfStatement
        if (value instanceof Long) return true;
        if (value == Undefined.value()) return true;

        return false;
    }

    public static GremlinPartitionKey.GremlinPartitionKeyBuilder builder() {
        return new GremlinPartitionKey.GremlinPartitionKeyBuilder();
    }

    public GremlinPartitionKey(GremlinPartitionKeyBuilder builder) {
        this.fieldName = builder.fieldName;
        setValue(builder.value);
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GremlinPartitionKey)) return false;

        GremlinPartitionKey other = (GremlinPartitionKey) o;

        if (isNotEqual(fieldName, other.fieldName)) return false;
        //noinspection RedundantIfStatement
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
        private Object value;

        GremlinPartitionKeyBuilder() {
        }

        public GremlinPartitionKey.GremlinPartitionKeyBuilder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public GremlinPartitionKey.GremlinPartitionKeyBuilder value(Object value) {
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
