// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosDatabaseRequestOptions;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import com.azure.graph.bulk.sample.CosmosDBGremlinExecutor;
import com.azure.graph.bulk.sample.DatabaseSettings;
import com.azure.graph.bulk.sample.GenerateDomainSamples;
import com.azure.graph.bulk.sample.GremlinCluster;
import com.azure.graph.bulk.sample.GremlinExecutionException;
import com.azure.graph.bulk.sample.GremlinExecutor;
import com.azure.graph.bulk.sample.GremlinResultReader;
import com.azure.graph.bulk.sample.GremlinSource;
import com.azure.graph.bulk.sample.UploadWithBulkLoader;
import com.azure.graph.bulk.sample.model.PersonVertex;
import com.azure.graph.bulk.sample.model.RelationshipEdge;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UploadWithBulkLoaderIT {

    private CosmosClient client;

    @BeforeEach
    @AfterEach
    void setupAndTeardown() {

        // Delete database from CosmosDB
        client = new CosmosClientBuilder()
                .endpoint(DatabaseSettings.HOST)
                .key(DatabaseSettings.MASTER_KEY)
                .contentResponseOnWriteEnabled(true)
                .consistencyLevel(ConsistencyLevel.SESSION)
                .buildClient();

        // Delete database
        try {
            CosmosDatabaseResponse dbResp =
                    client.getDatabase(DatabaseSettings.DATABASE_NAME).delete(new CosmosDatabaseRequestOptions());
        } catch (CosmosException e) {
        }
    }

    @Test
    void UploadWithBulkLoaderTest() throws GremlinExecutionException {
        // Generate vertices and edges for testing
        List<PersonVertex> vertices = GenerateDomainSamples.getVertices(10);
        List<RelationshipEdge> edges = GenerateDomainSamples.getEdges(vertices, 5);

        // Upload
        UploadWithBulkLoader<PersonVertex, RelationshipEdge> loader = new UploadWithBulkLoader<>();
        loader.uploadDocuments(vertices, edges);

        // Read data from CosmosDB
        GremlinResultReader gremlinResultReader = new GremlinResultReader();
        GremlinExecutor gremlinExecutor = new CosmosDBGremlinExecutor(new GremlinCluster());

        gremlinExecutor.connect();
        GremlinExecutor.GremlinExecutorResult results =
                gremlinExecutor.executeString("g.V().hasLabel('PERSON')");
        List<GremlinSource> gremlinVertices = gremlinResultReader.createResponseFromResultList(results.results);

        // Verify data upserted is equal to data read using tinkerpop
        assertEquals(vertices.size(), gremlinVertices.size());
        assertEquals(vertices.get(0).getId(), gremlinVertices.get(0).getId());
        assertEquals("PERSON", gremlinVertices.get(0).getLabel());
        assertEquals("vertex", gremlinVertices.get(0).getType());
        assertEquals(vertices.get(0).getCountry(), gremlinVertices.get(0).getProperties().get("country"));
    }
}
