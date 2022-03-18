// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample.model;

import com.azure.graph.bulk.impl.annotations.GremlinEdge;
import com.azure.graph.bulk.impl.annotations.GremlinEdgeVertex;
import com.azure.graph.bulk.impl.annotations.GremlinEdgeVertex.Direction;
import com.azure.graph.bulk.impl.annotations.GremlinLabel;
import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;

@GremlinEdge(partitionKeyFieldName = "country")
public class RelationshipEdge {
    @GremlinEdgeVertex(Direction = Direction.DESTINATION)
    public GremlinEdgeVertexInfo destinationVertexInfo;
    @GremlinEdgeVertex(Direction = Direction.SOURCE)
    public GremlinEdgeVertexInfo sourceVertexInfo;
    @GremlinLabel
    public String relationshipType;

    public RelationshipEdge(RelationshipEdge.RelationshipEdgeBuilder builder) {
        destinationVertexInfo = builder.destinationVertexInfo;
        sourceVertexInfo = builder.sourceVertexInfo;
        relationshipType = builder.relationshipType;
    }

    public static RelationshipEdge.RelationshipEdgeBuilder builder() {
        return new RelationshipEdge.RelationshipEdgeBuilder();
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof RelationshipEdge)) return false;
        RelationshipEdge other = (RelationshipEdge) o;

        if (isNotEqual(destinationVertexInfo, other.destinationVertexInfo)) return false;
        if (isNotEqual(sourceVertexInfo, other.sourceVertexInfo)) return false;
        if (isNotEqual(relationshipType, other.relationshipType)) return false;

        return true;
    }

    private boolean isNotEqual(Object source, Object other) {
        if (source == null && other == null) return false;
        if (source == null) return true;
        return !source.equals(other);
    }


    public int hashCode() {
        int result = 59 + (destinationVertexInfo == null ? 43 : destinationVertexInfo.hashCode());
        result = result * 59 + (sourceVertexInfo == null ? 43 : sourceVertexInfo.hashCode());
        result = result * 59 + (relationshipType == null ? 43 : relationshipType.hashCode());
        return result;
    }

    public static class RelationshipEdgeBuilder {
        private GremlinEdgeVertexInfo destinationVertexInfo;
        private GremlinEdgeVertexInfo sourceVertexInfo;
        private String relationshipType;

        RelationshipEdgeBuilder() {
        }

        public RelationshipEdge.RelationshipEdgeBuilder destinationVertexInfo(GremlinEdgeVertexInfo destinationVertexInfo) {
            this.destinationVertexInfo = destinationVertexInfo;
            return this;
        }

        public RelationshipEdge.RelationshipEdgeBuilder sourceVertexInfo(GremlinEdgeVertexInfo sourceVertexInfo) {
            this.sourceVertexInfo = sourceVertexInfo;
            return this;
        }

        public RelationshipEdge.RelationshipEdgeBuilder relationshipType(String relationshipType) {
            this.relationshipType = relationshipType;
            return this;
        }

        public RelationshipEdge build() {
            return new RelationshipEdge(this);
        }
    }
}
