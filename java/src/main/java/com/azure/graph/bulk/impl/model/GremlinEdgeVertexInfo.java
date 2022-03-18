// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.model;

import com.azure.graph.bulk.impl.annotations.GremlinId;
import com.azure.graph.bulk.impl.annotations.GremlinPartitionKey;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Objects;

public class GremlinEdgeVertexInfo {
    private String id;
    private String label;
    private String partitionKey;

    public GremlinEdgeVertexInfo() {
    }

    public GremlinEdgeVertexInfo(String id) {
        this.id = id;
    }

    public GremlinEdgeVertexInfo(GremlinVertex vertex) {
        id = vertex.getId();
        label = vertex.getLabel();
        partitionKey = vertex.getPartitionKey().getValue();
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

        GremlinEdgeVertexInfo result = new GremlinEdgeVertexInfo();

        Class<?> clazz = from.getClass();

        Class<com.azure.graph.bulk.impl.annotations.GremlinVertex> annotationClass =
                com.azure.graph.bulk.impl.annotations.GremlinVertex.class;
        if (!clazz.isAnnotationPresent(annotationClass)) {
            throw new IllegalArgumentException(
                    "Class " + clazz.getSimpleName() + " is missing GremlinVertex annotation");
        }

        result.label = clazz.getAnnotation(
                com.azure.graph.bulk.impl.annotations.GremlinVertex.class).label();

        for (Field field : FieldUtils.getAllFields(clazz)) {
            if (field.isAnnotationPresent(GremlinId.class)) {
                try {
                    result.id = (String) field.get(from);
                } catch (IllegalAccessException e) {
                    throw new ObjectConversionException(e);
                }
            }

            if (field.isAnnotationPresent(GremlinPartitionKey.class)) {
                try {
                    result.partitionKey = (String) field.get(from);
                } catch (IllegalAccessException e) {
                    throw new ObjectConversionException(e);
                }
            }
        }
        return result;
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

        return Objects.equals(c.getId(), this.getId());
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

    public String getPartitionKey() {
        return this.partitionKey;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    public GremlinEdgeVertexInfo(GremlinEdgeVertexInfoBuilder builder) {
        this.id = builder.id;
        this.label = builder.label;
        this.partitionKey = builder.partitionKey;
    }

    public static class GremlinEdgeVertexInfoBuilder {
        private String id;
        private String label;
        private String partitionKey;

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

        public GremlinEdgeVertexInfo.GremlinEdgeVertexInfoBuilder partitionKey(String partitionKey) {
            this.partitionKey = partitionKey;
            return this;
        }

        public GremlinEdgeVertexInfo build() {
            return new GremlinEdgeVertexInfo(this);
        }
    }
}
