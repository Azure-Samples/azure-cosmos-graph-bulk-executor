// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.model;

import com.azure.graph.bulk.impl.annotations.GremlinId;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

public class GremlinEdgeVertexInfo {
    private String id;
    private String label;
    private GremlinPartitionKey partitionKey;

    public GremlinEdgeVertexInfo(GremlinVertex vertex) {
        id = vertex.getId();
        label = vertex.getLabel();
        partitionKey = vertex.getPartitionKey();
    }

    public GremlinEdgeVertexInfo(GremlinEdgeVertexInfoBuilder builder) {
        this.id = builder.id;
        this.label = builder.label;
        this.partitionKey = builder.partitionKey;
    }

    /**
     * Pulls values required for a GremlinEdge document off of an Object that is defined as a GremlinVertex
     *
     * @param from Instance of the class annotated with @GremlinVertex to pull values from
     * @return GremlinEdgeVertexInfo containing all the required data to successfully create a link between the
     * Vertex and another Vertex
     */
    public static GremlinEdgeVertexInfo fromGremlinVertex(Object from) {
        if (from instanceof GremlinVertex) {
            return new GremlinEdgeVertexInfo((GremlinVertex) from);
        }

        GremlinEdgeVertexInfoBuilder builder = GremlinEdgeVertexInfo.builder();

        Class<?> clazz = from.getClass();

        Class<com.azure.graph.bulk.impl.annotations.GremlinVertex> annotationClass =
                com.azure.graph.bulk.impl.annotations.GremlinVertex.class;
        if (!clazz.isAnnotationPresent(annotationClass)) {
            throw new IllegalArgumentException(
                    "Class " + clazz.getSimpleName() + " is missing GremlinVertex annotation");
        }

        builder.label(clazz.getAnnotation(
                com.azure.graph.bulk.impl.annotations.GremlinVertex.class).label());

        for (Field field : FieldUtils.getAllFields(clazz)) {
            if (field.isAnnotationPresent(GremlinId.class)) {
                try {
                    builder.id((String) field.get(from));
                } catch (IllegalAccessException e) {
                    throw new ObjectConversionException(e);
                }
            }

            if (field.isAnnotationPresent(com.azure.graph.bulk.impl.annotations.GremlinPartitionKey.class)) {
                try {
                    Object rawObject = field.get(from);
                    if (rawObject instanceof GremlinPartitionKey)
                        builder.partitionKey((GremlinPartitionKey) rawObject);
                    else {
                        builder.partitionKey(GremlinPartitionKey.builder().value(rawObject).build());
                    }
                } catch (IllegalAccessException e) {
                    throw new ObjectConversionException(e);
                }
            }
        }
        return builder.build();
    }

    public void validate() {
        if (id == null || id.isBlank()) throw new IllegalStateException("Missing ID on GremlinEdge");

        if (label == null || label.isBlank()) throw new IllegalStateException(
                String.format("Missing label on GremlinEdgeVertexInfo: %s", id));

        if (partitionKey == null) throw new IllegalStateException(
                String.format("Missing Partition Key on GremlinEdgeVertexInfo ID: %s, Label: %s",
                        this.id, this.label));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof GremlinEdgeVertexInfo)) {
            return false;
        }

        GremlinEdgeVertexInfo c = (GremlinEdgeVertexInfo) o;

        if (isNotEqual(c.id, this.id)) return false;
        if (isNotEqual(c.label, this.label)) return false;
        if (isNotEqual(c.partitionKey, this.partitionKey)) return false;

        return true;
    }

    private boolean isNotEqual(Object source, Object other) {
        if (source == null && other == null) return false;
        if (source == null) return true;
        return !source.equals(other);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    public static GremlinEdgeVertexInfo.GremlinEdgeVertexInfoBuilder builder() {
        return new GremlinEdgeVertexInfo.GremlinEdgeVertexInfoBuilder();
    }

    public String getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public GremlinPartitionKey getPartitionKey() {
        return this.partitionKey;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPartitionKey(GremlinPartitionKey partitionKey) {
        this.partitionKey = partitionKey;
    }

    public static class GremlinEdgeVertexInfoBuilder {
        private String id;
        private String label;
        private GremlinPartitionKey partitionKey;

        GremlinEdgeVertexInfoBuilder() {
        }

        public GremlinEdgeVertexInfo.GremlinEdgeVertexInfoBuilder id(String id) {
            this.id = id;
            return this;
        }

        public GremlinEdgeVertexInfo.GremlinEdgeVertexInfoBuilder label(String label) {
            this.label = label;
            return this;
        }

        public GremlinEdgeVertexInfo.GremlinEdgeVertexInfoBuilder partitionKey(GremlinPartitionKey partitionKey) {
            this.partitionKey = partitionKey;
            return this;
        }

        public GremlinEdgeVertexInfo build() {
            return new GremlinEdgeVertexInfo(this);
        }
    }
}
