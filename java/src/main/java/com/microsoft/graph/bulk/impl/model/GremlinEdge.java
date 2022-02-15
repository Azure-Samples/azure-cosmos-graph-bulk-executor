package com.microsoft.graph.bulk.impl.model;

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

    //TODO: Evaluate impacts to changing this to be a HashMap<String, Serializable>
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

    //TODO: expand the validation logic to include id and label
    public void validate() {
        if (partitionKey == null) throw new IllegalStateException(
                String.format("Missing Partition Key on GremlinEdge ID: %s, Source ID: %s, Destination ID: %s",
                        this.id, this.sourceVertexInfo.getId(), this.getDestinationVertexInfo().getId()));
        partitionKey.validate();
    }
}
