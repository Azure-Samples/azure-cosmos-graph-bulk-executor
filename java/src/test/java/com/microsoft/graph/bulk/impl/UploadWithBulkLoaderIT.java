package com.microsoft.graph.bulk.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosDatabaseRequestOptions;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import com.microsoft.graph.bulk.sample.CosmosDBGremlinExecutor;
import com.microsoft.graph.bulk.sample.DatabaseSettings;
import com.microsoft.graph.bulk.sample.GenerateDomainSamples;
import com.microsoft.graph.bulk.sample.GremlinCluster;
import com.microsoft.graph.bulk.sample.GremlinExecutor;
import com.microsoft.graph.bulk.sample.GremlinResultReader;
import com.microsoft.graph.bulk.sample.UploadWithBulkLoader;
import com.microsoft.graph.bulk.sample.model.PersonVertex;
import com.microsoft.graph.bulk.sample.model.RelationshipEdge;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UploadWithBulkLoaderIT {

    private CosmosClient client;

    @SneakyThrows
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
            CosmosDatabaseResponse dbResp  = client.getDatabase(DatabaseSettings.DATABASE_NAME).delete(new CosmosDatabaseRequestOptions());
        }
        catch (CosmosException e) {
        }
    }

    @SneakyThrows
    @Test
    void UploadWithBulkLoaderTest() {
        // Generate vertices and edges for testing
        var vertices = GenerateDomainSamples.getVertices(10);
        var edges = GenerateDomainSamples.getEdges(vertices, 5);

        // Upload
        var loader = new UploadWithBulkLoader<PersonVertex, RelationshipEdge>();
        loader.uploadDocuments(vertices, edges);

        // Read data from CosmosDB
        GremlinResultReader gremlinResultReader = new GremlinResultReader();
        GremlinExecutor gremlinExecutor = new CosmosDBGremlinExecutor(new GremlinCluster());

        gremlinExecutor.connect();
        var results = gremlinExecutor.executeString("g.V().hasLabel('PERSON')");
        var gremlinVertices = gremlinResultReader.createResponseFromResultList(results.results);

        // Verify data upserted is equal to data read using tinkerpop
        assertEquals(vertices.size(), gremlinVertices.size());
        assertEquals(vertices.get(0).getId(), gremlinVertices.get(0).getId());
        assertEquals("PERSON", gremlinVertices.get(0).getLabel());
        assertEquals("vertex", gremlinVertices.get(0).getType());
        assertEquals(vertices.get(0).getCountry(), gremlinVertices.get(0).getProperties().get("country"));

    }
}
