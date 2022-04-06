// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.tinkerpop;

import org.apache.tinkerpop.gremlin.driver.Result;

import java.util.List;
import java.util.Map;

public interface GremlinExecutor {
    /**
     * Connect to the executor.
     */
    void connect();

    /**
     * Determine of the executor is connected. Return <code>true</code> if it is.
     *
     * @return <code>true</code> if executor is connected.
     */
    boolean isConnected();

    /**
     * Close the connection of the executor.
     */
    void close();

    /**
     * Execute a given query string.
     *
     * @param gremlinQuery query.
     * @return execution result.
     * @throws GremlinExecutionException when there are execution errors.
     */
    GremlinExecutorResult executeString(final String gremlinQuery) throws GremlinExecutionException;

    class GremlinExecutorResult {
        public List<Result> results;
        public Map<String, Object> statusAttributes;

        public List<Result> getResults() {
            return this.results;
        }

        public Map<String, Object> getStatusAttributes() {
            return this.statusAttributes;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

        public void setStatusAttributes(Map<String, Object> statusAttributes) {
            this.statusAttributes = statusAttributes;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof GremlinExecutor.GremlinExecutorResult)) return false;
            GremlinExecutor.GremlinExecutorResult other = (GremlinExecutor.GremlinExecutorResult) o;
            if (isNotEqual(results, other.results)) return false;
            if (isNotEqual(statusAttributes, other.statusAttributes)) return false;

            return true;
        }

        private boolean isNotEqual(Object source, Object other) {
            if (source == null && other == null) return false;
            if (source == null) return true;
            return !source.equals(other);
        }

        public int hashCode() {
            int result = 59 + (results == null ? 43 : results.hashCode());
            result = result * 59 + (statusAttributes == null ? 43 : statusAttributes.hashCode());
            return result;
        }

        public GremlinExecutorResult(List<Result> results, Map<String, Object> statusAttributes) {
            this.results = results;
            this.statusAttributes = statusAttributes;
        }
    }
}
