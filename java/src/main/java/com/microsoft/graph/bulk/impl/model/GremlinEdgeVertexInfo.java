package com.microsoft.graph.bulk.impl.model;

import com.microsoft.graph.bulk.impl.annotations.GremlinId;
import com.microsoft.graph.bulk.impl.annotations.GremlinPartitionKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
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
    @SneakyThrows
    public static GremlinEdgeVertexInfo fromGremlinVertex(Object from) {
        if (from instanceof GremlinVertex) {
            return new GremlinEdgeVertexInfo((GremlinVertex) from);
        }

        var result = new GremlinEdgeVertexInfo();

        Class<?> clazz = from.getClass();

        var annotationClass = com.microsoft.graph.bulk.impl.annotations.GremlinVertex.class;
        if (!clazz.isAnnotationPresent(annotationClass)) {
            throw new IllegalArgumentException(
                    "Class " + clazz.getSimpleName() + " is missing GremlinVertex annotation");
        }

        result.label = clazz.getAnnotation(
                com.microsoft.graph.bulk.impl.annotations.GremlinVertex.class).label();


        for (Field field : FieldUtils.getAllFields(clazz)) {
            if (field.isAnnotationPresent(GremlinId.class)) {
                result.id = (String) field.get(from);
            }

            if (field.isAnnotationPresent(GremlinPartitionKey.class)) {
                result.partitionKey = (String) field.get(from);
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
}
