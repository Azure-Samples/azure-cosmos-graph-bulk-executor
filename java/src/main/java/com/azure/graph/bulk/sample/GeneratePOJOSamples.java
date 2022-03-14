// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample;

import com.azure.graph.bulk.impl.model.GremlinEdge;
import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import com.azure.graph.bulk.impl.model.GremlinPartitionKey;
import com.azure.graph.bulk.impl.model.GremlinVertex;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.azure.graph.bulk.sample.SeedGenerationValues.*;

public class GeneratePOJOSamples {
    private GeneratePOJOSamples() {
        throw new IllegalStateException("Utility class, should not be constructed");
    }

    @SneakyThrows
    public static List<GremlinVertex> getVertices(int volume) {
        var random = SecureRandom.getInstanceStrong();
        return IntStream.range(1, volume + 1).mapToObj(
                        i -> generateVertex(random))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @SneakyThrows
    public static List<GremlinEdge> getEdges(List<GremlinVertex> vertices, int factor) {
        var edges = new ArrayList<GremlinEdge>();
        var random = SecureRandom.getInstanceStrong();
        for (var vertex : vertices) {
            var volume = random.nextInt(factor) + 1;

            for (int i = 1; i <= volume; i++) {
                var randomRelationshipType = RelationshipTypes[
                        random.nextInt(RelationshipTypes.length - 1)];

                var edge = GremlinEdge.builder()
                        .id(UUID.randomUUID().toString())
                        .sourceVertexInfo(GremlinEdgeVertexInfo.fromGremlinVertex(vertex))
                        .destinationVertexInfo(getRandomVertex(random, vertex.getId(), vertices))
                        .partitionKey(vertex.getPartitionKey())
                        .label(randomRelationshipType)
                        .properties(new HashMap<>())
                        .build();
                edges.add(edge);
            }
        }
        return edges;
    }

    private static GremlinEdgeVertexInfo getRandomVertex(Random random, String ignoreId, List<GremlinVertex> vertices) {
        GremlinEdgeVertexInfo vertex = null;
        while (vertex == null) {
            var potentialVertex = vertices.get(random.nextInt(vertices.size() - 1));
            if (!Objects.equals(potentialVertex.getId(), ignoreId)) {
                vertex = GremlinEdgeVertexInfo.fromGremlinVertex(potentialVertex);
            }
        }
        return vertex;
    }

    private static GremlinVertex generateVertex(Random random) {
        var firstName = firstNames[random.nextInt(firstNames.length - 1)];
        var lastName = lastNames[random.nextInt(lastNames.length - 1)];
        var country = countries[random.nextInt(countries.length - 1)];
        var emailProvider = emailProviders[random.nextInt(emailProviders.length - 1)];

        var vertex = GremlinVertex.builder()
                .id(UUID.randomUUID().toString())
                .label("PERSON")
                .properties(new HashMap<>())
                .partitionKey(new GremlinPartitionKey("country", country))
                .build();

        vertex.addProperty("firstName", firstName);
        vertex.addProperty("lastName", lastName);
        vertex.addProperty("ElectronicMail", String.format("%s.%s@%s.com", firstName, lastName, emailProvider));
        return vertex;
    }
}
