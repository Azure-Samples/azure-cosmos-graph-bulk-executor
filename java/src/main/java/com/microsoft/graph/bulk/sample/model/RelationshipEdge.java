package com.microsoft.graph.bulk.sample.model;

import com.microsoft.graph.bulk.impl.annotations.GremlinEdge;
import com.microsoft.graph.bulk.impl.annotations.GremlinEdgeVertex;
import com.microsoft.graph.bulk.impl.annotations.GremlinEdgeVertex.Direction;
import com.microsoft.graph.bulk.impl.annotations.GremlinLabel;
import com.microsoft.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import lombok.Builder;
import lombok.Data;

//TODO: Create a sample edge to demonstrate how a client specific domain class can be decorated to produce a
// GremlinEdge
@Data
@Builder
@GremlinEdge(partitionKeyFieldName = "country")
public class RelationshipEdge {
    @GremlinEdgeVertex(Direction = Direction.DESTINATION)
    public GremlinEdgeVertexInfo destinationVertexInfo;
    @GremlinEdgeVertex(Direction = Direction.SOURCE)
    public GremlinEdgeVertexInfo sourceVertexInfo;
    @GremlinLabel
    public String relationshipType;

    public RelationshipEdge(GremlinEdgeVertexInfo source, GremlinEdgeVertexInfo destination, String type) {
        destinationVertexInfo = destination;
        sourceVertexInfo = source;
        relationshipType = type;
    }
}
