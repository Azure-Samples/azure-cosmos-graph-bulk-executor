package com.azure.graph.bulk.impl.annotations;

import com.azure.graph.bulk.sample.model.PersonVertex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
class VertexAnnotationValidatorTest {
    VertexAnnotationValidator validator;

    @BeforeAll
    void setup() {
        validator = new VertexAnnotationValidator();
    }

    @Test
    void ClassLevelLabelPassesValidation() {
        List<String> results = validator.validate(PersonVertex.class);

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
        List<String> results = validator.validate(MissingLabel.class);

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
        List<String> results = validator.validate(FieldLevelLabel.class);

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
        List<String> results = validator.validate(MethodLevelLabel.class);

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
        List<String> results = validator.validate(MethodAndFieldLevelLabel.class);

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
        List<String> results = validator.validate(ClassAndFieldLevelLabel.class);

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
        List<String> results = validator.validate(AllTheLevelsLabel.class);

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
        List<String> results = validator.validate(NoPartitionKey.class);

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
        List<String> results = validator.validate(TooManyPartitionKeys.class);

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
        List<String> results = validator.validate(NoId.class);

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
        List<String> results = validator.validate(TooManyIds.class);

        assertEquals(1, results.size());
        assertEquals(VertexAnnotationValidator.GREMLIN_ID_INVALID, results.get(0));
    }
}
