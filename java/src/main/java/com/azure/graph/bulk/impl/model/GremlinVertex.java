// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

@Data
@Builder
@EqualsAndHashCode
public class GremlinVertex {
    private String id;
    private String label;
    private GremlinPartitionKey partitionKey;
    private HashMap<String, Object> properties;

    public void addProperty(String key, Object value) {
        addProperty(key, value, false);
    }

    public void addProperty(String key, Object value, boolean isPartitionKey) {
        if (value == null) return;

        if (isPartitionKey) {
            partitionKey = new GremlinPartitionKey(key, value.toString());
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
}
