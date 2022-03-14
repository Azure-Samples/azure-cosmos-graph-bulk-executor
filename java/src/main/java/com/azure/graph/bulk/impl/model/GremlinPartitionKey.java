// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class GremlinPartitionKey {
    private String fieldName;
    private String value;

    public void validate() {
        if (fieldName == null || fieldName.isBlank())
            throw new IllegalStateException("Field name for partition key is missing");
        if (value == null || value.isBlank()) throw new IllegalStateException("Partition key value is missing");
    }
}
