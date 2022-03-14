// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample;

import lombok.experimental.Delegate;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerV2d0;

public class GremlinCluster {

    @Delegate
    private final Cluster cluster;

    /**
     * Connection to CosmosDB cluster using Gremlin API
     */
    public GremlinCluster() {
        String username = String.format("/dbs/%s/colls/%s",
                DatabaseSettings.DATABASE_NAME,
                DatabaseSettings.CONTAINER_NAME);
        cluster = Cluster.build()
                .addContactPoint(DatabaseSettings.CONTACT_POINT)
                .port(DatabaseSettings.PORT)
                .enableSsl(DatabaseSettings.SSL_ENABLED)
                .credentials(username, DatabaseSettings.MASTER_KEY)
                .serializer(new GraphSONMessageSerializerV2d0())
                .maxConnectionPoolSize(DatabaseSettings.MAX_CONNECTION_POOL_SIZE)
                .maxWaitForConnection(DatabaseSettings.MAX_WAIT_FOR_CONNECTION)
                .create();
    }
}