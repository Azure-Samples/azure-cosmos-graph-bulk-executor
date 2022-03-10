package com.microsoft.graph.bulk.sample;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.driver.exception.ResponseException;

@Slf4j
public class GremlinExecutionException extends Exception {
  public GremlinExecutionException(String e) {

    log.error(e);
  }

  public GremlinExecutionException(Exception e) {
    log.error(e.getMessage());
  }

  public GremlinExecutionException(ResponseException re, Map<String, Object> reStatusAttributes) {
    log.error(re.getMessage());
  }
}
