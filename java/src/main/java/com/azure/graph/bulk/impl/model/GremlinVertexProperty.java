// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode
public class GremlinVertexProperty {
    private Object value;
    private String id;

    public GremlinVertexProperty(Object value) {
        this.value = value;
        id = UUID.randomUUID().toString();
    }
}
