package com.azure.graph.bulk.impl.annotations;

import com.azure.graph.bulk.impl.annotations.GremlinEdgeVertex.Direction;
import com.azure.graph.bulk.impl.model.GremlinEdgeVertexInfo;
import com.azure.graph.bulk.sample.model.RelationshipEdge;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EdgeAnnotationValidatorTest {
    @Test
    void ClassLevelLabelPassesValidation() {
        EdgeAnnotationValidator validator = new EdgeAnnotationValidator();

        List<String> results = validator.validateEdgeClass(RelationshipEdge.class);

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
        EdgeAnnotationValidator validator = new EdgeAnnotationValidator();

        List<String> results = validator.validateEdgeClass(MissingLabel.class);

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
        EdgeAnnotationValidator validator = new EdgeAnnotationValidator();

        List<String> results = validator.validateEdgeClass(FieldLevelLabel.class);

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
        EdgeAnnotationValidator validator = new EdgeAnnotationValidator();

        List<String> results = validator.validateEdgeClass(MethodLevelLabel.class);

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
        EdgeAnnotationValidator validator = new EdgeAnnotationValidator();

        List<String> results = validator.validateEdgeClass(MethodAndFieldLevelLabel.class);

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
        EdgeAnnotationValidator validator = new EdgeAnnotationValidator();

        List<String> results = validator.validateEdgeClass(ClassAndFieldLevelLabel.class);

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
        EdgeAnnotationValidator validator = new EdgeAnnotationValidator();

        List<String> results = validator.validateEdgeClass(AllTheLevelsLabel.class);

        assertEquals(1, results.size());
        assertEquals(EdgeAnnotationValidator.GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION, results.get(0));
    }
}
