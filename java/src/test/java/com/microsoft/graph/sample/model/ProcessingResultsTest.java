package com.microsoft.graph.sample.model;

import com.microsoft.graph.bulk.sample.model.ProcessingResults;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessingResultsTest {

    @SneakyThrows
    @Test
    void emptySerialization() {
        var processingResults = new ProcessingResults();
        var jsonString = processingResults.toJsonString();
        assertNotNull(jsonString);

        assertTrue(jsonString.contains("\"startTime\":"));
        assertTrue(jsonString.contains("\"endTime\":0"));
        assertTrue(jsonString.contains("\"durationInNanoSeconds\":0"));
        assertTrue(jsonString.contains("\"durationInMinutes\":0"));
        assertTrue(jsonString.contains("\"vertexCount\":0"));
        assertTrue(jsonString.contains("\"edgeCount\":0"));
        assertTrue(jsonString.contains("\"states\":[]"));
    }

    @SneakyThrows
    @Test
    void startingAndStopping() {
        var processingResults = new ProcessingResults();

        // Add some distance between start and stop
        Thread.sleep(10);

        processingResults.end();

        var jsonString = processingResults.toJsonString();

        // Has an endTime that isn't 0
        assertTrue(jsonString.contains("\"endTime\":"));
        assertFalse(jsonString.contains("\"endTime\":0"));

        // Has a duration that isn't 0
        assertTrue(jsonString.contains("\"durationInNanoSeconds\":"));
        assertTrue(jsonString.contains("\"durationInMinutes\":"));

        assertFalse(jsonString.contains("\"durationInNanoSeconds\":0"));
        assertFalse(jsonString.contains("\"durationInMinutes\":0"));
    }

    @SneakyThrows
    @Test
    void settingCounts() {
        var processingResults = new ProcessingResults();
        processingResults.setCounts(100, 500);
        var jsonString = processingResults.toJsonString();

        // Has a vertexCount that is matches the count set
        assertTrue(jsonString.contains("\"vertexCount\":100"));

        // Has an edgeCount that is matches the count set
        assertTrue(jsonString.contains("\"edgeCount\":500"));
    }

    @SneakyThrows
    @Test
    void transitionState() {
        var processingResults = new ProcessingResults();
        processingResults.setCounts(100, 500);

        //Simulate some state transitions
        processingResults.transitionState("Adding Counts");
        Thread.sleep(10);
        processingResults.transitionState("Loading to Data Store");
        Thread.sleep(10);
        processingResults.end();

        var jsonString = processingResults.toJsonString();

        // Has a vertexCount that is matches the count set
        assertTrue(jsonString.contains("\"states\":[{\"stateName\":\"Adding Counts\","));
        assertTrue(jsonString.contains("\"stateName\":\"Loading to Data Store\","));
    }
}
