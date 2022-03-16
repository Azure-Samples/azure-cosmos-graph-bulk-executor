// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.sample.model;

import com.azure.graph.bulk.sample.model.ProcessingResults;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessingResultsTest {

    @Test
    void emptySerialization() throws JsonProcessingException {
        ProcessingResults processingResults = new ProcessingResults();
        String jsonString = processingResults.toJsonString();
        assertNotNull(jsonString);

        assertTrue(jsonString.contains("\"startTime\":"));
        assertTrue(jsonString.contains("\"endTime\":0"));
        assertTrue(jsonString.contains("\"durationInNanoSeconds\":0"));
        assertTrue(jsonString.contains("\"durationInMinutes\":0"));
        assertTrue(jsonString.contains("\"vertexCount\":0"));
        assertTrue(jsonString.contains("\"edgeCount\":0"));
        assertTrue(jsonString.contains("\"states\":[]"));
    }

    @Test
    void startingAndStopping() throws InterruptedException, JsonProcessingException {
        ProcessingResults processingResults = new ProcessingResults();

        // Add some distance between start and stop
        Thread.sleep(10);

        processingResults.end();

        String jsonString = processingResults.toJsonString();

        // Has an endTime that isn't 0
        assertTrue(jsonString.contains("\"endTime\":"));
        assertFalse(jsonString.contains("\"endTime\":0"));

        // Has a duration that isn't 0
        assertTrue(jsonString.contains("\"durationInNanoSeconds\":"));
        assertTrue(jsonString.contains("\"durationInMinutes\":"));

        assertFalse(jsonString.contains("\"durationInNanoSeconds\":0"));
        assertFalse(jsonString.contains("\"durationInMinutes\":0"));
    }

    @Test
    void settingCounts() throws JsonProcessingException {
        ProcessingResults processingResults = new ProcessingResults();
        processingResults.setCounts(100, 500);
        String jsonString = processingResults.toJsonString();

        // Has a vertexCount that is matches the count set
        assertTrue(jsonString.contains("\"vertexCount\":100"));

        // Has an edgeCount that is matches the count set
        assertTrue(jsonString.contains("\"edgeCount\":500"));
    }

    @Test
    void transitionState() throws InterruptedException, JsonProcessingException {
        ProcessingResults processingResults = new ProcessingResults();
        processingResults.setCounts(100, 500);

        //Simulate some state transitions
        processingResults.transitionState("Adding Counts");
        Thread.sleep(10);
        processingResults.transitionState("Loading to Data Store");
        Thread.sleep(10);
        processingResults.end();

        String jsonString = processingResults.toJsonString();

        // Has a vertexCount that is matches the count set
        assertTrue(jsonString.contains("\"states\":[{\"stateName\":\"Adding Counts\","));
        assertTrue(jsonString.contains("\"stateName\":\"Loading to Data Store\","));
    }
}
