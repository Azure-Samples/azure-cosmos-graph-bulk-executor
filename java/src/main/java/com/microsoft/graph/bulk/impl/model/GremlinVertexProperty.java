package com.microsoft.graph.bulk.impl.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class GremlinVertexProperty {
    //TODO: Evaluate impacts to changing this to be Serializable instead of just Object
    private Object value;
    private String id;

    public GremlinVertexProperty(Object value) {
        this.value = value;
        //TODO: Validate if this should be a deterministic UUID, and if so what values should be included?
        // If it can/should be deterministic instead we should look at removing this class structure and instead
        // have the ID value determined at serialization time. If not deterministic, we should expose the ID value
        // to allow for the client user to manipulate it.
        id = UUID.randomUUID().toString();
    }
}
