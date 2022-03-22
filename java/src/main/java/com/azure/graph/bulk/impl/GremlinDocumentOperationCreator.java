// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.cosmos.implementation.JsonSerializable;
import com.azure.cosmos.models.CosmosBulkOperations;
import com.azure.cosmos.models.CosmosItemOperation;
import com.azure.cosmos.models.PartitionKey;
import com.azure.graph.bulk.impl.model.DocumentSerializationException;
import com.azure.graph.bulk.impl.model.GremlinEdge;
import com.azure.graph.bulk.impl.model.GremlinVertex;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

public class GremlinDocumentOperationCreator {
    private final ObjectMapper mapper;

    public GremlinDocumentOperationCreator(GremlinDocumentCreatorBuilder builder) {
        this.mapper = builder.mapper;
    }

    public static GremlinDocumentCreatorBuilder builder() {
        return new GremlinDocumentCreatorBuilder();
    }

    public Stream<CosmosItemOperation> getVertexCreateOperations(Stream<Object> vertices) {
        return vertices.map(v -> {
            GremlinVertex vertex = (v instanceof GremlinVertex) ?
                    (GremlinVertex) v :
                    ObjectToVertex.toGremlinVertex(v);
            vertex.validate();
            return getVertexCreateOperation(vertex);
        });
    }

    public Stream<CosmosItemOperation> getVertexUpsertOperations(Stream<Object> vertices) {
        return vertices.map(v -> {
            GremlinVertex vertex = (v instanceof GremlinVertex) ?
                    (GremlinVertex) v :
                    ObjectToVertex.toGremlinVertex(v);
            vertex.validate();
            return getVertexUpsertOperation(vertex);
        });
    }

    public CosmosItemOperation getVertexCreateOperation(GremlinVertex vertex) {
        PartitionKey partitionKey = new PartitionKey(vertex.getPartitionKey().getValue());
        try {
            return CosmosBulkOperations.getCreateItemOperation(
                    new JsonSerializable(mapper.writeValueAsString(vertex)),
                    partitionKey);
        } catch (JsonProcessingException e) {
            throw new DocumentSerializationException(e);
        }
    }

    private CosmosItemOperation getVertexUpsertOperation(GremlinVertex vertex) {
        PartitionKey partitionKey = new PartitionKey(vertex.getPartitionKey().getValue());
        try {
            return CosmosBulkOperations.getUpsertItemOperation(
                    new JsonSerializable(mapper.writeValueAsString(vertex)),
                    partitionKey);
        } catch (JsonProcessingException e) {
            throw new DocumentSerializationException(e);
        }
    }

    public Stream<CosmosItemOperation> getEdgeCreateOperations(Stream<Object> edges) {
        return edges.map(e -> {
            GremlinEdge edge = e instanceof GremlinEdge ? (GremlinEdge) e : ObjectToEdge.toGremlinEdge(e);
            edge.validate();
            return getEdgeCreateOperation(edge);
        });
    }

    public Stream<CosmosItemOperation> getEdgeUpsertOperations(Stream<Object> edges) {
        return edges.map(e -> {
            GremlinEdge edge = e instanceof GremlinEdge ? (GremlinEdge) e : ObjectToEdge.toGremlinEdge(e);
            edge.validate();
            return getEdgeUpsertOperation(edge);
        });
    }

    private CosmosItemOperation getEdgeCreateOperation(GremlinEdge edge) {
        PartitionKey partitionKey = new PartitionKey(edge.getPartitionKey().getValue());
        try {
            return CosmosBulkOperations.getCreateItemOperation(
                    new JsonSerializable(mapper.writeValueAsString(edge)),
                    partitionKey);
        } catch (JsonProcessingException e) {
            throw new DocumentSerializationException(e);
        }
    }

    private CosmosItemOperation getEdgeUpsertOperation(GremlinEdge edge) {
        PartitionKey partitionKey = new PartitionKey(edge.getPartitionKey().getValue());
        try {
            return CosmosBulkOperations.getUpsertItemOperation(
                    new JsonSerializable(mapper.writeValueAsString(edge)),
                    partitionKey);
        } catch (JsonProcessingException e) {
            throw new DocumentSerializationException(e);
        }
    }

    public static class GremlinDocumentCreatorBuilder {
        GremlinDocumentCreatorBuilder() {

        }

        private ObjectMapper mapper;

        public GremlinDocumentCreatorBuilder mapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public GremlinDocumentOperationCreator build() {
            return new GremlinDocumentOperationCreator(this);
        }
    }
}
