package com.microsoft.graph.bulk.sample;

import com.microsoft.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import com.microsoft.graph.bulk.sample.model.PersonVertex;
import com.microsoft.graph.bulk.sample.model.RelationshipEdge;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.microsoft.graph.bulk.sample.SeedGenerationValues.*;

public class GenerateDomainSamples {
    private GenerateDomainSamples() {
        throw new IllegalStateException("Utility class, should not be constructed");
    }

    @SneakyThrows
    public static List<PersonVertex> getVertices(int volume) {
        var random = SecureRandom.getInstanceStrong();
        return IntStream.range(1, volume + 1).mapToObj(
                        i -> generatePerson(random))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @SneakyThrows
    public static List<RelationshipEdge> getEdges(List<PersonVertex> vertices, int factor) {
        var edges = new ArrayList<RelationshipEdge>();
        var random = SecureRandom.getInstanceStrong();
        for (var vertex : vertices) {
            var volume = random.nextInt(factor) + 1;
            for (int i = 1; i <= volume; i++) {

                edges.add(new RelationshipEdge(
                        GremlinEdgeVertexInfo.fromGremlinVertex(vertex),
                        getRandomVertex(random, vertex.id, vertices),
                        RelationshipTypes[random.nextInt(RelationshipTypes.length - 1)]));
            }
        }
        return edges;
    }

    private static GremlinEdgeVertexInfo getRandomVertex(Random random, String sourceId, List<PersonVertex> vertices) {
        GremlinEdgeVertexInfo vertex = null;
        while (vertex == null) {
            var potentialVertex = vertices.get(random.nextInt(vertices.size() - 1));
            if (!Objects.equals(potentialVertex.getId(), sourceId)) {
                vertex = GremlinEdgeVertexInfo.fromGremlinVertex(potentialVertex);
            }
        }
        return vertex;
    }

    private static PersonVertex generatePerson(Random random) {
        var firstName = firstNames[random.nextInt(firstNames.length - 1)];
        var lastName = lastNames[random.nextInt(lastNames.length - 1)];
        var country = countries[random.nextInt(countries.length - 1)];
        var emailProvider = emailProviders[random.nextInt(emailProviders.length - 1)];
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
