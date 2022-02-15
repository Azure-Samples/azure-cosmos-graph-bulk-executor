package com.microsoft.graph.bulk.sample;

import com.microsoft.graph.bulk.impl.model.GremlinEdge;
import com.microsoft.graph.bulk.impl.model.GremlinVertex;
import com.microsoft.graph.bulk.sample.model.PersonVertex;
import com.microsoft.graph.bulk.sample.model.ProcessingResults;
import com.microsoft.graph.bulk.sample.model.RelationshipEdge;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

@Slf4j
public class Main {

    private static final ProcessingResults results = new ProcessingResults();

    @SneakyThrows
    public static void main(String[] args) {
        try {
            var options = getOptions();
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
        var vertices = GenerateDomainSamples.getVertices(
                Integer.parseInt(cmd.getOptionValue(ArgNames.VERTEX_COUNT)));
        results.transitionState("Build sample edges");

        var edges = GenerateDomainSamples.getEdges(
                vertices,
                Integer.parseInt(cmd.getOptionValue(ArgNames.EDGE_MAX)));

        results.setCounts(vertices.size(), edges.size());

        executeWithDomain(vertices, edges);
    }

    private static void runPOJOSample(CommandLine cmd) {
        results.transitionState("Build sample vertices");
        var vertices = GeneratePOJOSamples.getVertices(
                Integer.parseInt(cmd.getOptionValue(ArgNames.VERTEX_COUNT)));
        results.transitionState("Build sample edges");

        var edges = GeneratePOJOSamples.getEdges(
                vertices,
                Integer.parseInt(cmd.getOptionValue(ArgNames.EDGE_MAX)));

        results.setCounts(vertices.size(), edges.size());

        executeWithPOJO(vertices, edges);
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
                "Indicates if the bulk executor sample should run the sample using Domain structures. If not present, the sample will using the raw GremlinVertex and GremlinEdge POJOs. This will allow you to compare the two different implementation methods.");
        return options;
    }

    private static void executeWithDomain(Iterable<PersonVertex> vertices, Iterable<RelationshipEdge> edges) {
        results.transitionState("Configure Database");
        var loader = new UploadWithBulkLoader<PersonVertex, RelationshipEdge>();
        results.transitionState("Write Documents");
        loader.uploadDocuments(vertices, edges);
    }

    private static void executeWithPOJO(Iterable<GremlinVertex> vertices, Iterable<GremlinEdge> edges) {
        results.transitionState("Configure Database");
        var loader = new UploadWithBulkLoader<GremlinVertex, GremlinEdge>();
        results.transitionState("Write Documents");
        loader.uploadDocuments(vertices, edges);
    }
}
