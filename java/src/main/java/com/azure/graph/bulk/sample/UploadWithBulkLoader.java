// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerRequestOptions;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import com.azure.cosmos.models.CosmosItemOperation;
import com.azure.cosmos.models.ThroughputProperties;
import com.azure.graph.bulk.impl.BulkGremlinObjectMapper;
import com.azure.graph.bulk.impl.GremlinDocumentOperationCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;


public class UploadWithBulkLoader {
    private static final Logger log = LoggerFactory.getLogger(UploadWithBulkLoader.class);
    private final CosmosAsyncClient client;

    private CosmosAsyncDatabase database;
    private CosmosAsyncContainer container;

    private final GremlinDocumentOperationCreator documentOperationCreator;

    public UploadWithBulkLoader() {
        client = new CosmosClientBuilder()
                .endpoint(DatabaseSettings.HOST)
                .key(DatabaseSettings.MASTER_KEY)
                .contentResponseOnWriteEnabled(true)
                .consistencyLevel(ConsistencyLevel.SESSION)
                .buildAsyncClient();

        createDatabaseIfNotExists();
        createContainerIfNotExists();

        documentOperationCreator = GremlinDocumentOperationCreator.builder()
                .mapper(BulkGremlinObjectMapper.getBulkGremlinObjectMapper())
                .build();
    }

    private void createDatabaseIfNotExists() {
        log.info("Create database " + DatabaseSettings.DATABASE_NAME + " if not exists.");

        //  Create database if not exists
        //  <CreateDatabaseIfNotExists>
        Mono<CosmosDatabaseResponse> databaseIfNotExists =
                client.createDatabaseIfNotExists(DatabaseSettings.DATABASE_NAME);
        databaseIfNotExists.flatMap(databaseResponse -> {
            database = client.getDatabase(databaseResponse.getProperties().getId());
            log.info("Checking database " + database.getId() + " completed!\n");
            return Mono.empty();
        }).block();
        //  </CreateDatabaseIfNotExists>
    }

    private void createContainerIfNotExists() {
        log.info("Create container " + DatabaseSettings.CONTAINER_NAME + " if not exists.");

        //  Create container if not exists
        //  <CreateContainerIfNotExists>

        CosmosContainerProperties containerProperties = new CosmosContainerProperties(
                DatabaseSettings.CONTAINER_NAME, DatabaseSettings.PARTITION_KEY_PATH);
        ThroughputProperties throughputProperties = ThroughputProperties.createManualThroughput(DatabaseSettings.THROUGHPUT);
        Mono<CosmosContainerResponse> containerIfNotExists = database.createContainerIfNotExists(containerProperties, throughputProperties);

        //  Create container with the configured RU/s
        CosmosContainerResponse cosmosContainerResponse = containerIfNotExists.block();
        assert cosmosContainerResponse != null;
        container = database.getContainer(cosmosContainerResponse.getProperties().getId());
        //  </CreateContainerIfNotExists>

        //Modify existing container
        containerProperties = cosmosContainerResponse.getProperties();
        Mono<CosmosContainerResponse> propertiesReplace = container.replace(containerProperties, new CosmosContainerRequestOptions());
        propertiesReplace.flatMap(containerResponse -> {
            log.info("setupContainer(): Container " + container.getId() + " in " + database.getId() +
                    " has been updated with it's new properties.");
            return Mono.empty();
        }).onErrorResume((exception) -> {
            log.error("setupContainer(): Unable to update properties for container " + container.getId() +
                    " in database " + database.getId() +
                    ". e: " + exception.getLocalizedMessage());
            return Mono.empty();
        }).block();
    }

    public void uploadDocuments(
            Stream vertices, Stream edges, boolean createDocs) {

        Stream<CosmosItemOperation> operations;
        if (createDocs) {
            operations = documentOperationCreator.getVertexCreateOperations(vertices);
            operations = Stream.concat(operations, documentOperationCreator.getEdgeCreateOperations(edges));
        } else {
            operations = documentOperationCreator.getVertexUpsertOperations(vertices);
            operations = Stream.concat(operations, documentOperationCreator.getEdgeUpsertOperations(edges));
        }

        container.executeBulkOperations(Flux.fromStream(operations))
                .filter(r -> r.getException() != null || r.getResponse().getStatusCode() > 299)
                .doOnNext(r -> {
                    if (r.getException() != null) {
                        log.error("Failed with exception {0}", r.getException());
                    } else {
                        log.error("Failed with status code {}", r.getResponse().getStatusCode());
                    }
                })
                .blockLast();
    }
}
