// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample;

import com.azure.graph.bulk.impl.model.GremlinEdge;
import com.azure.graph.bulk.impl.model.GremlinVertex;
import com.azure.graph.bulk.sample.model.PersonVertex;
import com.azure.graph.bulk.sample.model.ProcessingResults;
import com.azure.graph.bulk.sample.model.RelationshipEdge;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.util.List;
import java.util.stream.Stream;

public class Main {
    private static final ProcessingResults results = new ProcessingResults();

    public static void main(String[] args) {
        try {
            Options options = getOptions();
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption(ArgNames.DOMAIN_SAMPLE)) {
                runDomainSample(cmd);
            } else {
                runPOJOSample(cmd);
            }

        } catch (Exception e) {
            results.failure(e);
        } finally {
            results.end();
            System.exit(0);
        }
    }

    private static void runDomainSample(CommandLine cmd) {
        results.transitionState("Build sample vertices");
        List<PersonVertex> vertices = GenerateDomainSamples.getVertices(
                Integer.parseInt(cmd.getOptionValue(ArgNames.VERTEX_COUNT)));
        results.transitionState("Build sample edges");

        List<RelationshipEdge> edges = GenerateDomainSamples.getEdges(
                vertices,
                Integer.parseInt(cmd.getOptionValue(ArgNames.EDGE_MAX)));

        results.setCounts(vertices.size(), edges.size());

        executeWithDomain(vertices.stream(), edges.stream(), cmd.hasOption(ArgNames.CREATE_DOCS));
    }

    private static void runPOJOSample(CommandLine cmd) {
        results.transitionState("Build sample vertices");
        List<GremlinVertex> vertices = GeneratePOJOSamples.getVertices(
                Integer.parseInt(cmd.getOptionValue(ArgNames.VERTEX_COUNT)));
        results.transitionState("Build sample edges");

        List<GremlinEdge> edges = GeneratePOJOSamples.getEdges(
                vertices,
                Integer.parseInt(cmd.getOptionValue(ArgNames.EDGE_MAX)));

        results.setCounts(vertices.size(), edges.size());

        executeWithPOJO(vertices.stream(), edges.stream(), cmd.hasOption(ArgNames.CREATE_DOCS));
    }

    private static Options getOptions() {
        Options options = new Options();

        options.addOption(
                "v",
                ArgNames.VERTEX_COUNT,
                true,
                "How many vertices to generate for the sample");
        options.addOption(
                "e",
                ArgNames.EDGE_MAX,
                true,
                "Max of edges to attach to each vertex");
        options.addOption(
                "d",
                ArgNames.DOMAIN_SAMPLE,
                false,
                "Indicates if the bulk executor sample should run the sample using Domain structures. If not present, the sample will use the raw GremlinVertex and GremlinEdge POJOs. This will allow you to compare the two different implementation methods.");
        options.addOption(
                "c",
                ArgNames.CREATE_DOCS,
                false,
                "Indicates if the bulk executor sample should run the sample using create item operations. If not preset, the sample will use upsert item operations instead.");
        return options;
    }

    private static void executeWithDomain(Stream<PersonVertex> vertices,
                                          Stream<RelationshipEdge> edges,
                                          boolean createDocs) {
        results.transitionState("Configure Database");
        UploadWithBulkLoader loader = new UploadWithBulkLoader();
        results.transitionState("Write Documents");
        loader.uploadDocuments(vertices, edges, createDocs);
    }

    private static void executeWithPOJO(Stream<GremlinVertex> vertices,
                                        Stream<GremlinEdge> edges,
                                        boolean createDocs) {
        results.transitionState("Configure Database");
        UploadWithBulkLoader loader = new UploadWithBulkLoader();
        results.transitionState("Write Documents");
        loader.uploadDocuments(vertices, edges, createDocs);
    }
}
