// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample;

import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.driver.exception.ResponseException;

import java.util.Map;

@Slf4j
public class GremlinExecutionException extends Exception {
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
