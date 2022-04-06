// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.annotations;

import com.azure.graph.bulk.impl.annotations.GremlinEdgeVertex.Direction;
import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import com.azure.graph.bulk.sample.model.RelationshipEdge;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
class EdgeAnnotationValidatorTest {
    EdgeAnnotationValidator validator;

    @BeforeAll
    void setup() {
        validator = new EdgeAnnotationValidator();
    }

    @Test
    void ClassLevelLabelPassesValidation() {
        List<String> results = validator.validate(RelationshipEdge.class);

        assertEquals(0, results.size());
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field")
    static class MissingLabel {
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination;
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source;
    }

    @Test
    void MissingLabelFailsValidation() {
        List<String> results = validator.validate(MissingLabel.class);

        assertEquals(1, results.size());
        assertEquals(EdgeAnnotationValidator.GREMLIN_LABEL_INVALID, results.get(0));
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field")
    static class FieldLevelLabel {
        @GremlinLabel
        public String label;
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination;
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source;
    }

    @Test
    void FieldLevelLabelPassesValidation() {
        List<String> results = validator.validate(FieldLevelLabel.class);

        assertEquals(0, results.size());
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field")
    static class MethodLevelLabel {
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination;
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source;

        @GremlinLabelGetter
        public String label() {
            return "TheLabel";
        }
    }

    @Test
    void MethodLevelLabelPassesValidation() {
        List<String> results = validator.validate(MethodLevelLabel.class);

        assertEquals(0, results.size());
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field")
    static class MethodAndFieldLevelLabel {
        @GremlinLabel
        public String label;
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination;
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source;

        @GremlinLabelGetter
        public String label() {
            return "TheLabel";
        }
    }

    @Test
    void MethodAndFieldLabelFailsValidation() {
        List<String> results = validator.validate(MethodAndFieldLevelLabel.class);

        assertEquals(1, results.size());
        assertEquals(EdgeAnnotationValidator.GREMLIN_LABEL_INVALID, results.get(0));
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field", label = "TheLabel")
    static class ClassAndFieldLevelLabel {
        @GremlinLabel
        public String label;
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination;
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source;
    }

    @Test
    void ClassAndFieldLabelFailsValidation() {
        List<String> results = validator.validate(ClassAndFieldLevelLabel.class);

        assertEquals(1, results.size());
        assertEquals(EdgeAnnotationValidator.GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION, results.get(0));
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field", label = "TheLabel")
    static class AllTheLevelsLabel {
        @GremlinLabel
        public String label;
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination;
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source;

        @GremlinLabelGetter
        public String label() {
            return "TheLabel";
        }
    }

    @Test
    void AllTheLevelsLabelFailsValidation() {
        List<String> results = validator.validate(AllTheLevelsLabel.class);

        assertEquals(1, results.size());
        assertEquals(EdgeAnnotationValidator.GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION, results.get(0));
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field", label = "TheLabel")
    static class MissingSourceEdge {
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination;
    }

    @Test
    void MissingSourceEdgeFailsValidation() {
        List<String> results = validator.validate(MissingSourceEdge.class);

        assertEquals(1, results.size());
        assertEquals(String.format(EdgeAnnotationValidator.GREMLIN_EDGE_VERTEX_MISSING, Direction.SOURCE)
                , results.get(0));
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field", label = "TheLabel")
    static class MissingDestinationEdge {
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source;
    }

    @Test
    void MissingDestinationEdgeFailsValidation() {
        List<String> results = validator.validate(MissingDestinationEdge.class);

        assertEquals(1, results.size());
        assertEquals(String.format(EdgeAnnotationValidator.GREMLIN_EDGE_VERTEX_MISSING, Direction.DESTINATION)
                , results.get(0));
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field", label = "TheLabel")
    static class ToManyDestinationEdges {
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source;
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination;
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination2;
    }

    @Test
    void ToManyDestinationEdgesFailsValidation() {
        List<String> results = validator.validate(ToManyDestinationEdges.class);

        assertEquals(1, results.size());
        assertEquals(String.format(EdgeAnnotationValidator.GREMLIN_EDGE_VERTEX_TO_MANY, Direction.DESTINATION)
                , results.get(0));
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field", label = "TheLabel")
    static class ToManySourceEdges {
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source;
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source2;
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination;
    }

    @Test
    void ToManySourceEdgesFailsValidation() {
        List<String> results = validator.validate(ToManySourceEdges.class);

        assertEquals(1, results.size());
        assertEquals(String.format(EdgeAnnotationValidator.GREMLIN_EDGE_VERTEX_TO_MANY, Direction.SOURCE)
                , results.get(0));
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field", label = "TheLabel")
    static class HasPartitionKey {
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source;
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination;
        @GremlinPartitionKey
        public String partitionKey;
    }

    @Test
    void HasPartitionKeyFailsValidation() {
        List<String> results = validator.validate(HasPartitionKey.class);

        assertEquals(1, results.size());
        assertEquals(EdgeAnnotationValidator.GREMLIN_EDGE_PARTITION_KEY, results.get(0));
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field", label = "TheLabel")
    static class ToManyIds {
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source;
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination;
        @GremlinId
        public String id;
        @GremlinId
        public String id2;
    }

    @Test
    void ToManyIdsFailsValidation() {
        List<String> results = validator.validate(ToManyIds.class);

        assertEquals(1, results.size());
        assertEquals(EdgeAnnotationValidator.GREMLIN_EDGE_ID, results.get(0));
    }

    @GremlinEdge(partitionKeyFieldName = "pk-field", label = "TheLabel")
    static class HasId {
        @GremlinEdgeVertex(direction = Direction.SOURCE)
        public GremlinEdgeVertexInfo source;
        @GremlinEdgeVertex(direction = Direction.DESTINATION)
        public GremlinEdgeVertexInfo destination;
        @GremlinId
        public String id;
    }

    @Test
    void HasIdPassesValidation() {
        List<String> results = validator.validate(HasId.class);

        assertEquals(0, results.size());
    }
}
