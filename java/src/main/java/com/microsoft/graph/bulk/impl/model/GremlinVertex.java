package com.microsoft.graph.bulk.impl.model;

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
    private HashMap<String, GremlinVertexProperty> properties;

    public void addProperty(String key, Object value) {
        addProperty(key, value, false);
    }

    public void addProperty(String key, Object value, boolean isPartitionKey) {
        if (value == null) return;

        if (isPartitionKey) {
            partitionKey = new GremlinPartitionKey(key, value.toString());
        } else {
            properties.put(key, new GremlinVertexProperty(value));
        }
    }

    //TODO: expand the validation logic to include id and label
    public void validate() {
        if (partitionKey == null) throw new IllegalStateException(
                String.format("Missing Partition Key on GremlinVertex %s", this.id));
        partitionKey.validate();
    }
}
