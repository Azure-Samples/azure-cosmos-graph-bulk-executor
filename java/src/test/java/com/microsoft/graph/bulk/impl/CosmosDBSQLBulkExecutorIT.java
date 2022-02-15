//package com.microsoft.graph;
//
//import com.microsoft.graph.GraphBulkExecutor;
//import com.microsoft.graph.model.GremlinEdgeVertexInfo;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import reactor.test.StepVerifier;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Slf4j
//class CosmosDBSQLBulkExecutorIT extends SchemaCommonITBase {
//    private GremlinExecutor gremlinExecutor;
//    private GremlinSchemaGraph gremlinSchemaGraph;
//    private GraphBulkExecutor<SchemaGraphNode, SchemaGraphEdge> cosmosDBGremlinExecutor;
//
//    private SchemaGraphProperties schemaGraphProperties;
//
//    private SlsBulkGremlinExecutors slsBulkGremlinExecutors;
//
//    private GremlinCluster gremlinCluster;
//
//    private SchemaSource schemaSource;
//
//    @BeforeEach
//    void setup() throws GremlinExecutionException {
//        GremlinResultReader gremlinResultReader = new GremlinResultReaderImpl();
//        gremlinExecutor = new CosmosDBGremlinExecutor(gremlinCluster, schemaGraphProperties);
//        cosmosDBGremlinExecutor = slsBulkGremlinExecutors.getSchemaSourceGremlinBulkExecutor(schemaSource);
//        gremlinSchemaGraph = new GremlinSchemaGraph(
//                gremlinExecutor,
//                gremlinResultReader,
//                schemaGraphProperties,
//                cosmosDBGremlinExecutor);
//
//        gremlinExecutor.connect();
//        gremlinExecutor.execute(g -> g.V().drop());
//    }
//
//    @AfterEach
//    void teardown() throws GremlinExecutionException {
//        gremlinExecutor.execute(g -> g.V().drop());
//    }
//
//    @Test
//    void testAddEdge() throws SchemaGraphException {
//        ClassificationNode foo = GraphTestUtils.simpleClassificationNode("foo");
//        ClassificationNode bar = GraphTestUtils.simpleClassificationNode("bar");
//        SchemaNode foo_1_0 = GraphTestUtils.simpleSchemaNode("foo", "1.0");
//        SchemaNode bar_2_3 = GraphTestUtils.simpleSchemaNode("bar", "2.3");
//        SchemaNode product = buildSchemaNode("product", "1.1", SchemaType.PRODUCT);
//
//        var inheritsEdge = new InheritsSchemaGraphEdge(GremlinEdgeVertexInfo.fromGremlinVertex(foo_1_0),
//                GremlinEdgeVertexInfo.fromGremlinVertex(bar_2_3), 0);
//
//        assertTrue(gremlinSchemaGraph.getClassification(foo.getClassification()).isEmpty());
//        assertTrue(gremlinSchemaGraph.getClassification(bar.getClassification()).isEmpty());
//        assertTrue(gremlinSchemaGraph.getSchema(foo_1_0.getId()).isEmpty());
//        assertTrue(gremlinSchemaGraph.getSchema(bar_2_3.getId()).isEmpty());
//        assertTrue(gremlinSchemaGraph.getSchema(product.getId()).isEmpty());
//        assertFalse(gremlinSchemaGraph.containsRelationship(inheritsEdge));
//
//        List<SchemaGraphNode> vertices = new ArrayList<>();
//
//        vertices.add(foo);
//        vertices.add(bar);
//        vertices.add(foo_1_0);
//        vertices.add(bar_2_3);
//        vertices.add(product);
//
//        List<SchemaGraphEdge> edges = new ArrayList<>();
//
//        edges.add(inheritsEdge);
//
//        var results = cosmosDBGremlinExecutor
//                .execute(vertices, edges)
//                .filter(r -> r.getException() != null || r.getResponse().getStatusCode() > 299)
//                .doOnNext(r -> {
//                    if (r.getException() != null) {
//                        CosmosDBSQLBulkExecutorIT.log.error("Failed with exception {}", r.getException().getMessage());
//                    } else {
//                        CosmosDBSQLBulkExecutorIT.log.error("Failed with status code {}", r.getResponse().getStatusCode());
//                    }
//                })
//                .log();
//
//        StepVerifier.create(results)
//                .expectNextCount(0)
//                .verifyComplete();
//
//        Optional<ClassificationNode> fooNode = gremlinSchemaGraph.getClassification(foo.getClassification());
//        assertFalse(fooNode.isEmpty());
//        assertEquals(foo.classification, fooNode.get().classification);
//
//        Optional<ClassificationNode> barNode = gremlinSchemaGraph.getClassification(bar.getClassification());
//        assertFalse(barNode.isEmpty());
//        assertEquals(bar.classification, barNode.get().classification);
//
//        Optional<SchemaNode> foo_1_0_node = gremlinSchemaGraph.getSchema(foo_1_0.getId());
//        assertFalse(foo_1_0_node.isEmpty());
//        assertEquals(foo_1_0, foo_1_0_node.get());
//
//        Optional<SchemaNode> bar_2_3_node = gremlinSchemaGraph.getSchema(bar_2_3.getId());
//        assertFalse(bar_2_3_node.isEmpty());
//        assertEquals(bar_2_3, bar_2_3_node.get());
//
//        Optional<SchemaNode> productNode = gremlinSchemaGraph.getSchema(product.getId());
//        assertFalse(productNode.isEmpty());
//        assertEquals(product, productNode.get());
//
//        assertTrue(gremlinSchemaGraph.containsRelationship(inheritsEdge));
//    }
//}
