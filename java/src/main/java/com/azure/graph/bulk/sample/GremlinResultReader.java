// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.driver.Result;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class GremlinResultReader {

    public static final String ID = "id";
    public static final String LABEL = "label";
    public static final String TYPE = "type";
    public static final String PROPERTIES = "properties";
    public static final String VALUE = "value";

    public GremlinSource createResponseFromResult(Result result) {
        final Map<String, Object> map = (Map<String, Object>) result.getObject();
        return createResponseFromMap(map);
    }

    /**
     * Create the GremlinSource response from a map of Tinker pop results
     * eg:
     * { "id" : "test", "type" : "vertex", "label" : "ingestSchema",
     * "properties" : { "name" : [ { "id" : "abc", "value" : "create-shot"}], "version" : [{ "id" : "def", "value" : "1.20"}]}
     * }
     *
     * @param map
     * @return GremlinSource
     */
    public GremlinSource createResponseFromMap(Map<String, Object> map) {
        GremlinSource gremlinSource = GremlinSource.builder().build();

        final Optional<Map<String, Object>> properties = Optional.ofNullable((Map<String, Object>) map.get(PROPERTIES));
        properties.ifPresent(props -> props.forEach(
                (key, value) -> gremlinSource.setProperty(key, this.readProperty(value))));
        gremlinSource.setId((String) map.get(ID));
        gremlinSource.setType((String) map.get(TYPE));
        gremlinSource.setLabel((String) map.get(LABEL));

        return gremlinSource;
    }

    public List<GremlinSource> createResponseFromResultList(List<Result> results) {
        return results.stream().map(this::createResponseFromResult).collect(Collectors.toList());
    }

    /**
     * Gremlin query returns the properties in ArrayList with one value
     * eg: value = [{ "id" : "def", "value" : "1.20"}]
     * response is 1.20
     *
     * @param value
     * @return Object
     */
    private Object readProperty(@NonNull Object value) {
        if (value instanceof String) {
            return value;
        }
        final List<Map<String, String>> mapList = (List<Map<String, String>>) value;
        if (mapList.size() > 1) {
            log.error("Properties values are more than 1 {} returned only first one and skipped rest of those", mapList);
        }
        return mapList.get(0).get(VALUE);
    }

}
