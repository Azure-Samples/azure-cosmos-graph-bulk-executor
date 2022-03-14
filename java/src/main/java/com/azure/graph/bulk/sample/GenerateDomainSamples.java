// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample;

import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import com.azure.graph.bulk.sample.model.PersonVertex;
import com.azure.graph.bulk.sample.model.RelationshipEdge;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.azure.graph.bulk.sample.SeedGenerationValues.*;

public class GenerateDomainSamples {
    private GenerateDomainSamples() {
        throw new IllegalStateException("Utility class, should not be constructed");
    }

    @SneakyThrows
    public static List<PersonVertex> getVertices(int volume) {
        SecureRandom random = SecureRandom.getInstanceStrong();
        return IntStream.range(1, volume + 1).mapToObj(
                        i -> generatePerson(random))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @SneakyThrows
    public static List<RelationshipEdge> getEdges(List<PersonVertex> vertices, int factor) {
        ArrayList<RelationshipEdge> edges = new ArrayList<>();
        SecureRandom random = SecureRandom.getInstanceStrong();
        for (PersonVertex vertex : vertices) {
            int volume = random.nextInt(factor) + 1;
            for (int i = 1; i <= volume; i++) {

                edges.add(new RelationshipEdge(
                        GremlinEdgeVertexInfo.fromGremlinVertex(vertex),
                        getRandomVertex(random, vertex.id, vertices),
                        SeedGenerationValues.RelationshipTypes[
                                random.nextInt(SeedGenerationValues.RelationshipTypes.length - 1)]));
            }
        }
        return edges;
    }

    private static GremlinEdgeVertexInfo getRandomVertex(Random random, String sourceId, List<PersonVertex> vertices) {
        GremlinEdgeVertexInfo vertex = null;
        while (vertex == null) {
            PersonVertex potentialVertex = vertices.get(random.nextInt(vertices.size() - 1));
            if (!Objects.equals(potentialVertex.getId(), sourceId)) {
                vertex = GremlinEdgeVertexInfo.fromGremlinVertex(potentialVertex);
            }
        }
        return vertex;
    }

    private static PersonVertex generatePerson(Random random) {
        String firstName = firstNames[random.nextInt(firstNames.length - 1)];
        String lastName = lastNames[random.nextInt(lastNames.length - 1)];
        String country = countries[random.nextInt(countries.length - 1)];
        String emailProvider = emailProviders[random.nextInt(emailProviders.length - 1)];

        return PersonVertex.builder()
                .id(UUID.randomUUID().toString())
                .isSpecial(false)
                .firstName(firstName)
                .lastName(lastName)
                .email(String.format("%s.%s@%s.com", firstName, lastName, emailProvider))
                .country(country)
                .build();
    }
}
