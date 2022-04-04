package com.azure.graph.bulk.impl.annotations;

import com.azure.graph.bulk.sample.model.PersonVertex;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VertexAnnotationValidatorTest {
    @Test
    void ClassLevelLabelPassesValidation() {
        VertexAnnotationValidator validator = new VertexAnnotationValidator();

        List<String> results = validator.validateVertexClass(PersonVertex.class);

        assertEquals(0, results.size());
    }

    @GremlinVertex
    static class MissingLabel {
        @GremlinId
        public String id;
        @GremlinPartitionKey
        public String partitionKey;
    }

    @Test
    void MissingLabelFailsValidation() {
        VertexAnnotationValidator validator = new VertexAnnotationValidator();

        List<String> results = validator.validateVertexClass(MissingLabel.class);

        assertEquals(1, results.size());
        assertEquals(VertexAnnotationValidator.GREMLIN_LABEL_INVALID, results.get(0));
    }

    @GremlinVertex
    static class FieldLevelLabel {
        @GremlinId
        public String id;
        @GremlinPartitionKey
        public String partitionKey;
        @GremlinLabel
        public String label;
    }

    @Test
    void FieldLevelLabelPassesValidation() {
        VertexAnnotationValidator validator = new VertexAnnotationValidator();

        List<String> results = validator.validateVertexClass(FieldLevelLabel.class);

        assertEquals(0, results.size());
    }

    @GremlinVertex
    static class MethodLevelLabel {
        @GremlinId
        public String id;
        @GremlinPartitionKey
        public String partitionKey;

        @GremlinLabelGetter
        public String label() {
            return "TheLabel";
        }
    }

    @Test
    void MethodLevelLabelPassesValidation() {
        VertexAnnotationValidator validator = new VertexAnnotationValidator();

        List<String> results = validator.validateVertexClass(MethodLevelLabel.class);

        assertEquals(0, results.size());
    }

    @GremlinVertex
    static class MethodAndFieldLevelLabel {
        @GremlinId
        public String id;
        @GremlinPartitionKey
        public String partitionKey;
        @GremlinLabel
        public String label;

        @GremlinLabelGetter
        public String label() {
            return "TheLabel";
        }
    }

    @Test
    void MethodAndFieldLabelFailsValidation() {
        VertexAnnotationValidator validator = new VertexAnnotationValidator();

        List<String> results = validator.validateVertexClass(MethodAndFieldLevelLabel.class);

        assertEquals(1, results.size());
        assertEquals(VertexAnnotationValidator.GREMLIN_LABEL_INVALID, results.get(0));
    }

    @GremlinVertex(label = "TheLabel")
    static class ClassAndFieldLevelLabel {
        @GremlinId
        public String id;
        @GremlinPartitionKey
        public String partitionKey;
        @GremlinLabel
        public String label;
    }

    @Test
    void ClassAndFieldLabelFailsValidation() {
        VertexAnnotationValidator validator = new VertexAnnotationValidator();

        List<String> results = validator.validateVertexClass(ClassAndFieldLevelLabel.class);

        assertEquals(1, results.size());
        assertEquals(VertexAnnotationValidator.GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION, results.get(0));
    }

    @GremlinVertex(label = "TheLabel")
    static class AllTheLevelsLabel {
        @GremlinId
        public String id;
        @GremlinPartitionKey
        public String partitionKey;
        @GremlinLabel
        public String label;

        @GremlinLabelGetter
        public String label() {
            return "TheLabel";
        }
    }

    @Test
    void AllTheLevelsLabelFailsValidation() {
        VertexAnnotationValidator validator = new VertexAnnotationValidator();

        List<String> results = validator.validateVertexClass(AllTheLevelsLabel.class);

        assertEquals(1, results.size());
        assertEquals(VertexAnnotationValidator.GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION, results.get(0));
    }

    @GremlinVertex(label = "TheLabel")
    static class NoPartitionKey {
        @GremlinId
        public String id;
    }

    @Test
    void NoPartitionKeyFailsValidation() {
        VertexAnnotationValidator validator = new VertexAnnotationValidator();

        List<String> results = validator.validateVertexClass(NoPartitionKey.class);

        assertEquals(1, results.size());
        assertEquals(VertexAnnotationValidator.GREMLIN_PARTITION_KEY_INVALID, results.get(0));
    }

    @GremlinVertex(label = "TheLabel")
    static class TooManyPartitionKeys {
        @GremlinId
        public String id;
        @GremlinPartitionKey
        public String partitionKey;
        @GremlinPartitionKey
        public String partitionKey2;
    }

    @Test
    void TooManyPartitionKeysFailsValidation() {
        VertexAnnotationValidator validator = new VertexAnnotationValidator();

        List<String> results = validator.validateVertexClass(TooManyPartitionKeys.class);

        assertEquals(1, results.size());
        assertEquals(VertexAnnotationValidator.GREMLIN_PARTITION_KEY_INVALID, results.get(0));
    }

    @GremlinVertex(label = "TheLabel")
    static class NoId {
        @GremlinPartitionKey
        public String partitionKey;
    }

    @Test
    void NoIdFailsValidation() {
        VertexAnnotationValidator validator = new VertexAnnotationValidator();

        List<String> results = validator.validateVertexClass(NoId.class);

        assertEquals(1, results.size());
        assertEquals(VertexAnnotationValidator.GREMLIN_ID_INVALID, results.get(0));
    }

    @GremlinVertex(label = "TheLabel")
    static class TooManyIds {
        @GremlinId
        public String id;
        @GremlinPartitionKey
        public String partitionKey;
        @GremlinId
        public String id2;
    }

    @Test
    void TooManyIdsFailsValidation() {
        VertexAnnotationValidator validator = new VertexAnnotationValidator();

        List<String> results = validator.validateVertexClass(TooManyIds.class);

        assertEquals(1, results.size());
        assertEquals(VertexAnnotationValidator.GREMLIN_ID_INVALID, results.get(0));
    }
}
