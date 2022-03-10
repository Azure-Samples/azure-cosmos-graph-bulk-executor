package com.microsoft.graph.bulk.sample;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
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
}