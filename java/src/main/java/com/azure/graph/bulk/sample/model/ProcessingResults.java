// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class ProcessingResults {
    private final static long nanoSecondsInAMinute = 60000000000l;

    @JsonAutoDetect(fieldVisibility = Visibility.ANY)
    private class StateTransition {
        private String stateName;
        private long startTime;
        private long endTime;
        private long durationInNanoSeconds;
        private float durationInMinutes;

        public StateTransition(String stateName) {
            this.stateName = stateName;
            startTime = System.nanoTime();
        }

        public void stop() {
            endTime = System.nanoTime();
            durationInNanoSeconds = endTime - startTime;
            durationInMinutes = (float) durationInNanoSeconds / ProcessingResults.nanoSecondsInAMinute;
        }
    }

    private long startTime;
    private long endTime;
    private long durationInNanoSeconds;
    private float durationInMinutes;
    private int vertexCount;
    private int edgeCount;
    private Exception exception;

    @JsonIgnore
    private StateTransition currentState;

    private ArrayList<StateTransition> states;

    public ProcessingResults() {
        states = new ArrayList<>();
        startTime = System.nanoTime();
    }

    public void transitionState(String newState) {
        if (currentState != null) {
            currentState.stop();
        }
        log.info("State transition: {}", newState);
        currentState = new StateTransition(newState);

        states.add(currentState);
    }

    public void setCounts(int vertexCount, int edgeCount) {
        this.vertexCount = vertexCount;
        this.edgeCount = edgeCount;
    }

    public void end() throws JsonProcessingException {
        if (currentState != null) {
            currentState.stop();
        }
        endTime = System.nanoTime();
        durationInNanoSeconds = endTime - startTime;
        durationInMinutes = (float) durationInNanoSeconds / ProcessingResults.nanoSecondsInAMinute;
        log.info("Processing complete. Results are: {}", this.toJsonString());
    }

    public void failure(Exception e) {
        log.error("Exception occurred: {}", e.getMessage());
        exception = e;
    }

    public String toJsonString() throws JsonProcessingException {
        var mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return mapper.writeValueAsString(this);
    }
}
