package com.azure.graph.bulk.impl.annotations;

import com.azure.graph.bulk.sample.model.PersonVertex;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VertexAnnotationValidatorTest {
    @Test
    void ClassLevelLabelPassesValidation() {
        AnnotationValidator validator = new AnnotationValidator();

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
        AnnotationValidator validator = new AnnotationValidator();

        List<String> results = validator.validateVertexClass(MissingLabel.class);

        assertEquals(AnnotationValidator.GREMLIN_LABEL_INVALID, results.get(0));
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
        AnnotationValidator validator = new AnnotationValidator();

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
        AnnotationValidator validator = new AnnotationValidator();

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
        AnnotationValidator validator = new AnnotationValidator();

        List<String> passed = validator.validateVertexClass(MethodAndFieldLevelLabel.class);

        assertEquals(AnnotationValidator.GREMLIN_LABEL_INVALID, passed.get(0));
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
        AnnotationValidator validator = new AnnotationValidator();

        List<String> results = validator.validateVertexClass(ClassAndFieldLevelLabel.class);

        assertEquals(AnnotationValidator.GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION, results.get(0));
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
        AnnotationValidator validator = new AnnotationValidator();

        List<String> results = validator.validateVertexClass(AllTheLevelsLabel.class);

        assertEquals(AnnotationValidator.GREMLIN_LABEL_INVALID_WITH_CLASS_ANNOTATION, results.get(0));
    }

    @GremlinVertex(label = "TheLabel")
    static class NoPartitionKey {
        @GremlinId
        public String id;
    }

    @Test
    void NoPartitionKeyFailsValidation() {
        AnnotationValidator validator = new AnnotationValidator();

        List<String> results = validator.validateVertexClass(NoPartitionKey.class);

        assertEquals(AnnotationValidator.GREMLIN_PARTITION_KEY_INVALID, results.get(0));
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
        AnnotationValidator validator = new AnnotationValidator();

        List<String> results = validator.validateVertexClass(TooManyPartitionKeys.class);

        assertEquals(AnnotationValidator.GREMLIN_PARTITION_KEY_INVALID, results.get(0));
    }

    @GremlinVertex(label = "TheLabel")
    static class NoId {
        @GremlinPartitionKey
        public String partitionKey;
    }

    @Test
    void NoIdFailsValidation() {
        AnnotationValidator validator = new AnnotationValidator();

        List<String> results = validator.validateVertexClass(NoId.class);

        assertEquals(AnnotationValidator.GREMLIN_ID_INVALID, results.get(0));
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
        AnnotationValidator validator = new AnnotationValidator();

        List<String> results = validator.validateVertexClass(TooManyIds.class);

        assertEquals(AnnotationValidator.GREMLIN_ID_INVALID, results.get(0));
    }
}
