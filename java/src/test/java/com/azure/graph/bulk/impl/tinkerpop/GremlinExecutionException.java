// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.tinkerpop;


import org.apache.tinkerpop.gremlin.driver.exception.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class GremlinExecutionException extends Exception {
    private static final Logger log = LoggerFactory.getLogger(GremlinExecutionException.class);

    public GremlinExecutionException(String e) {
        log.error(e);
    }

    public GremlinExecutionException(Exception e) {
        log.error(e.getMessage());
    }

    public GremlinExecutionException(ResponseException re, Map<String, Object> reStatusAttributes) {
        log.error(re.getMessage());
    }
}
