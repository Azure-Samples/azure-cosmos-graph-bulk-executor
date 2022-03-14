// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample.model;

import com.azure.graph.bulk.impl.annotations.GremlinEdge;
import com.azure.graph.bulk.impl.annotations.GremlinEdgeVertex;
import com.azure.graph.bulk.impl.annotations.GremlinEdgeVertex.Direction;
import com.azure.graph.bulk.impl.annotations.GremlinLabel;
import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import lombok.Builder;
import lombok.Data;

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
