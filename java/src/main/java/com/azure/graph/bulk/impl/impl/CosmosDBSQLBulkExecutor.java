// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.impl;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.implementation.JsonSerializable;
import com.azure.cosmos.models.CosmosBulkOperationResponse;
import com.azure.cosmos.models.CosmosBulkOperations;
import com.azure.cosmos.models.CosmosItemOperation;
import com.azure.cosmos.models.PartitionKey;
import com.azure.graph.bulk.impl.GraphBulkExecutor;
import com.azure.graph.bulk.impl.ObjectToEdge;
import com.azure.graph.bulk.impl.ObjectToVertex;
import com.azure.graph.bulk.impl.model.DocumentSerializationException;
import com.azure.graph.bulk.impl.model.GremlinEdge;
import com.azure.graph.bulk.impl.model.GremlinVertex;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class CosmosDBSQLBulkExecutor implements GraphBulkExecutor {
    private final CosmosAsyncContainer container;
    private final ObjectMapper mapper;
    private final boolean allowUpsert;

    public CosmosDBSQLBulkExecutor(CosmosDBSQLBulkExecutorBuilder builder) {
        this.container = builder.container;
        this.mapper = builder.mapper;
        this.allowUpsert = builder.allowUpsert;
    }

    public static CosmosDBSQLBulkExecutorBuilder builder() {
        return new CosmosDBSQLBulkExecutorBuilder();
    }

    public Flux<CosmosBulkOperationResponse<GraphBulkExecutor>> execute(
            Iterable vertices, Iterable edges) {
        if (vertices == null) vertices = new ArrayList<>();
        if (edges == null) edges = new ArrayList<>();

        Stream<CosmosItemOperation> vertexOperations = getVertexStream(vertices).map(this::getVertexOperation);
        Stream<CosmosItemOperation> edgeOperations = getEdgeStream(edges).map(this::getEdgeOperation);
        Stream<CosmosItemOperation> allOperations = Stream.concat(vertexOperations, edgeOperations);

        return container.executeBulkOperations(Flux.fromStream(allOperations));
    }

    private Stream<GremlinVertex> getVertexStream(Iterable vertices) {
        return StreamSupport.stream(vertices.spliterator(), true).map(v -> {
            GremlinVertex vertex = (v instanceof GremlinVertex) ?
                    (GremlinVertex) v :
                    ObjectToVertex.toGremlinVertex(v);
            vertex.validate();
            return vertex;
        });
    }

    private CosmosItemOperation getVertexOperation(GremlinVertex vertex) {
        PartitionKey partitionKey = new PartitionKey(vertex.getPartitionKey().getValue());
        try {
            if (allowUpsert)
                return CosmosBulkOperations.getUpsertItemOperation(
                        new JsonSerializable(mapper.writeValueAsString(vertex)),
                        partitionKey);

            return CosmosBulkOperations.getCreateItemOperation(
                    new JsonSerializable(mapper.writeValueAsString(vertex)),
                    partitionKey);
        } catch (JsonProcessingException e) {
            throw new DocumentSerializationException(e);
        }
    }

    private Stream<GremlinEdge> getEdgeStream(Iterable edges) {
        return StreamSupport.stream(edges.spliterator(), true).map(e -> {
            GremlinEdge edge = e instanceof GremlinEdge ? (GremlinEdge) e : ObjectToEdge.toGremlinEdge(e);
            edge.validate();
            return edge;
        });
    }

    private CosmosItemOperation getEdgeOperation(GremlinEdge edge) {
        PartitionKey partitionKey = new PartitionKey(edge.getPartitionKey().getValue());
        try {
            if (allowUpsert)
                return CosmosBulkOperations.getUpsertItemOperation(
                        new JsonSerializable(mapper.writeValueAsString(edge)),
                        partitionKey);

            return CosmosBulkOperations.getCreateItemOperation(
                    new JsonSerializable(mapper.writeValueAsString(edge)),
                    partitionKey);
        } catch (JsonProcessingException e) {
            throw new DocumentSerializationException(e);
        }
    }

    public static class CosmosDBSQLBulkExecutorBuilder {
        CosmosDBSQLBulkExecutorBuilder() {

        }

        private CosmosAsyncContainer container;
        private ObjectMapper mapper;
        private boolean allowUpsert;

        public CosmosDBSQLBulkExecutor.CosmosDBSQLBulkExecutorBuilder container(CosmosAsyncContainer container) {
            this.container = container;
            return this;
        }

        public CosmosDBSQLBulkExecutor.CosmosDBSQLBulkExecutorBuilder mapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public CosmosDBSQLBulkExecutor.CosmosDBSQLBulkExecutorBuilder allowUpsert(boolean allowUpsert) {
            this.allowUpsert = allowUpsert;
            return this;
        }

        public CosmosDBSQLBulkExecutor build() {
            return new CosmosDBSQLBulkExecutor(this);
        }
    }

}
