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

    /**
     * Used to convert the stream of objects provided into a stream of Cosmos Create Operations
     *
     * @param vertices Stream of objects that are either GremlinVertex objects, or domain objects with the
     *                 GremlinVertex annotations
     * @return Stream of Cosmos Item Operations
     */
    public Stream<CosmosItemOperation> getVertexCreateOperations(Stream<Object> vertices) {
        return vertices.map(this::getVertexCreateOperation);
    }

    /**
     * Used to convert the stream of objects provided into a stream of Cosmos Upsert Operations
     *
     * @param vertices Stream of objects that are either GremlinVertex objects, or domain objects with the
     *                 GremlinVertex annotations
     * @return Stream of Cosmos Item Operations
     */
    public Stream<CosmosItemOperation> getVertexUpsertOperations(Stream<Object> vertices) {
        return vertices.map(this::getVertexUpsertOperation);
    }

    /***
     * Converts the object provided into a Cosmos Create Operation
     *
     * @param vertex Either a GremlinVertex object or domain object with the GremlinVertex annotations
     * @return CosmosItemOperation to create the object with
     */
    public CosmosItemOperation getVertexCreateOperation(Object vertex) {
        GremlinVertex gremlinVertex = getVertexFromObject(vertex);

        PartitionKey partitionKey = new PartitionKey(gremlinVertex.getPartitionKey().getValue());
        try {
            return CosmosBulkOperations.getCreateItemOperation(
                    new JsonSerializable(mapper.writeValueAsString(gremlinVertex)),
                    partitionKey);
        } catch (JsonProcessingException e) {
            throw new DocumentSerializationException(e);
        }
    }

    /***
     * Converts the object provided into a Cosmos Upsert Operation
     *
     * @param vertex Either a GremlinVertex object or domain object with the GremlinVertex annotations
     * @return CosmosItemOperation to upsert the object with
     */
    public CosmosItemOperation getVertexUpsertOperation(Object vertex) {
        GremlinVertex gremlinVertex = getVertexFromObject(vertex);

        PartitionKey partitionKey = new PartitionKey(gremlinVertex.getPartitionKey().getValue());
        try {
            return CosmosBulkOperations.getUpsertItemOperation(
                    new JsonSerializable(mapper.writeValueAsString(gremlinVertex)),
                    partitionKey);
        } catch (JsonProcessingException e) {
            throw new DocumentSerializationException(e);
        }
    }

    private GremlinVertex getVertexFromObject(Object rawVertex) {
        GremlinVertex vertex = (rawVertex instanceof GremlinVertex) ?
                (GremlinVertex) rawVertex :
                ObjectToVertex.toGremlinVertex(rawVertex);

        vertex.validate();
        return vertex;
    }

    /**
     * Used to convert the stream of objects provided into a stream of Cosmos Create Operations
     *
     * @param edges Stream of objects that are either GremlinEdge objects, or domain objects with the
     *              GremlinEdge annotations
     * @return Stream of Cosmos Item Operations
     */
    public Stream<CosmosItemOperation> getEdgeCreateOperations(Stream<Object> edges) {
        return edges.map(this::getEdgeCreateOperation);
    }

    /**
     * Used to convert the stream of objects provided into a stream of Cosmos Upsert Operations
     *
     * @param edges Stream of objects that are either GremlinEdge objects, or domain objects with the
     *              GremlinEdge annotations
     * @return Stream of Cosmos Item Operations
     */
    public Stream<CosmosItemOperation> getEdgeUpsertOperations(Stream<Object> edges) {
        return edges.map(this::getEdgeUpsertOperation);
    }

    /***
     * Converts the object provided into a Cosmos Create Operation
     *
     * @param edge Either a GremlinVertex object or domain object with the GremlinVertex annotations
     * @return CosmosItemOperation to create the object with
     */
    public CosmosItemOperation getEdgeCreateOperation(Object edge) {
        GremlinEdge gremlinEdge = getEdgeFromObject(edge);

        PartitionKey partitionKey = new PartitionKey(gremlinEdge.getPartitionKey().getValue());
        try {
            return CosmosBulkOperations.getCreateItemOperation(
                    new JsonSerializable(mapper.writeValueAsString(gremlinEdge)),
                    partitionKey);
        } catch (JsonProcessingException e) {
            throw new DocumentSerializationException(e);
        }
    }

    private GremlinEdge getEdgeFromObject(Object e) {
        GremlinEdge edge = e instanceof GremlinEdge ? (GremlinEdge) e : ObjectToEdge.toGremlinEdge(e);
        edge.validate();
        return edge;
    }

    /***
     * Converts the object provided into a Cosmos Upsert Operation
     *
     * @param edge Either a GremlinVertex object or domain object with the GremlinVertex annotations
     * @return CosmosItemOperation to upsert the object with
     */
    public CosmosItemOperation getEdgeUpsertOperation(Object edge) {
        GremlinEdge gremlinEdge = getEdgeFromObject(edge);

        PartitionKey partitionKey = new PartitionKey(gremlinEdge.getPartitionKey().getValue());
        try {
            return CosmosBulkOperations.getUpsertItemOperation(
                    new JsonSerializable(mapper.writeValueAsString(gremlinEdge)),
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
