// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.impl.tinkerpop;

import java.util.HashMap;
import java.util.Map;

public class GremlinSource {
    private String id;
    private String label;
    private String type;
    Map<String, Object> properties;

    public void setProperty(String key, Object value) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        if (this.properties.containsKey(key) && value == null) {
            this.properties.remove(key);
        } else {
            this.properties.put(key, value);
        }
    }

    GremlinSource(GremlinSourceBuilder builder) {
        this.id = builder.id;
        this.label = builder.label;
        this.type = builder.type;
        this.properties = builder.properties;
    }

    public static GremlinSource.GremlinSourceBuilder builder() {
        return new GremlinSource.GremlinSourceBuilder();
    }

    public String getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public String getType() {
        return this.type;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GremlinSource)) return false;
        GremlinSource other = (GremlinSource) o;

        if (isNotEqual(id, other.id)) return false;
        if (isNotEqual(label, other.label)) return false;
        if (isNotEqual(type, other.type)) return false;
        //noinspection RedundantIfStatement
        if (isNotEqual(properties, other.properties)) return false;

        return true;
    }

    private boolean isNotEqual(Object source, Object other) {
        if (source == null && other == null) return false;
        if (source == null) return true;
        return !source.equals(other);
    }

    public int hashCode() {
        int result = 59 + (id == null ? 43 : id.hashCode());
        result = result * 59 + (label == null ? 43 : label.hashCode());
        result = result * 59 + (type == null ? 43 : type.hashCode());
        result = result * 59 + (properties == null ? 43 : properties.hashCode());
        return result;
    }

    public static class GremlinSourceBuilder {
        private String id;
        private String label;
        private String type;
        private Map<String, Object> properties;

        GremlinSourceBuilder() {
        }

        public GremlinSource.GremlinSourceBuilder id(String id) {
            this.id = id;
            return this;
        }

        public GremlinSource.GremlinSourceBuilder label(String label) {
            this.label = label;
            return this;
        }

        public GremlinSource.GremlinSourceBuilder type(String type) {
            this.type = type;
            return this;
        }

        public GremlinSource.GremlinSourceBuilder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public GremlinSource build() {
            return new GremlinSource(this);
        }
    }
}