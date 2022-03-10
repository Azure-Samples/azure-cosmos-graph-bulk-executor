package com.microsoft.graph.bulk.sample;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.tinkerpop.gremlin.driver.Result;

public interface GremlinExecutor {
  /**
   * Connect to the executor.
   */
  void connect();

  /**
   * Determine of the executor is connected. Return <code>true</code> if it is.
   *
   * @return <code>true</code> if executor is connected.
   */
  boolean isConnected();

  /**
   * Close the connection of the executor.
   */
  void close();

  /**
   * Execute a given query string.
   *
   * @param gremlinQuery query.
   * @return execution result.
   * @throws GremlinExecutionException when there are execution errors.
   */
  GremlinExecutorResult executeString(final String gremlinQuery) throws GremlinExecutionException;

  @Data
  @AllArgsConstructor
  public class GremlinExecutorResult {
    public List<Result> results;
    public Map<String, Object> statusAttributes;
  }
}
