// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl;

import com.azure.cosmos.models.CosmosBulkOperationResponse;
import reactor.core.publisher.Flux;

/**
 * Interface for classes capable of writing Edges and Vertices to a data store
 *
 * @param <V> Defines the class for the Vertices
 * @param <E> Defines the class for the Edges
 */
public interface GraphBulkExecutor {
    /**
     * Writes all vertices and edges to the data store
     *
     * @param vertices Vertices to write to the data store
     * @param edges    Edges to write to the data store
     * @return Results of the executions
     */
    Flux<CosmosBulkOperationResponse<GraphBulkExecutor>> execute(
            Iterable vertices, Iterable edges);
}
