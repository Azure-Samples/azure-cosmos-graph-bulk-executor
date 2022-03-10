package com.microsoft.graph.bulk.sample;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.apache.tinkerpop.gremlin.driver.exception.ResponseException;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CosmosDBGremlinExecutor implements GremlinExecutor {

  private final GremlinCluster cluster;
  private Client client;
  private final Long maxRetryAfterMs;

  public CosmosDBGremlinExecutor(GremlinCluster cluster) {
    this.cluster = cluster;
    maxRetryAfterMs = Long.valueOf(1000);
  }

  @Override
  @PostConstruct
  public void connect() {
    if (!isConnected()) {
      client = cluster.connect();
    }
  }

  @Override
  public boolean isConnected() {
    return client != null;
  }

  @Override
  public void close() {
    if (isConnected()) {
      client.close();
      client = null;
    }
  }

  private void verifyConnection() throws GremlinExecutionException {
    if (!isConnected()) {
      throw new GremlinExecutionException("Cannot execute gremlin query while disconnected. Call connect() to establish a connection to the Gremlin server.");
    }
  }

  @Override
  public GremlinExecutorResult executeString(final String gremlinQuery) throws GremlinExecutionException {
    verifyConnection();
    ResultSet resultSet = client.submit(gremlinQuery);
    return createGremlinExecutorResult(resultSet);
  }

  private GremlinExecutorResult createGremlinExecutorResult(ResultSet resultSet) throws GremlinExecutionException {
    List<Result> results = null;
    Map<String, Object> statusAttributes = null;
    try {
      results = resultSet.all().get();
      statusAttributes = resultSet.statusAttributes().get();
      log.debug(statusAttributes.toString());
    } catch (Exception e) {
      handleExecuteException(e);
    }

    return new GremlinExecutorResult(results, statusAttributes);
  }

  private void handleExecuteException(Exception e) throws GremlinExecutionException {
    Throwable cause = e.getCause();
    if (!(cause instanceof ResponseException)) {
      throw new GremlinExecutionException(e);
    }

    ResponseException re = (ResponseException) cause;
    Optional<Map<String, Object>> statusAttributes = re.getStatusAttributes();
    if (statusAttributes.isEmpty()) {
      throw new GremlinExecutionException(re);
    }

    Map<String, Object> reStatusAttributes = statusAttributes.get();
    log.debug(reStatusAttributes.toString());
    waitForRetryIfRetryHintProvided(reStatusAttributes);
    throw new GremlinExecutionException(re, reStatusAttributes);
  }

  private void waitForRetryIfRetryHintProvided(Map<String, Object> reStatusAttributes) {
    // https://docs.microsoft.com/en-us/azure/cosmos-db/gremlin-headers#samples
    String retryAfter = (String) reStatusAttributes.get("x-ms-retry-after-ms");
    if (StringUtils.isEmpty(retryAfter)) {
      return;
    }

    LocalTime localTime = LocalTime.parse(retryAfter);
    Duration duration = Duration.between(LocalTime.MIN, localTime);
    long retryAfterMs = duration.toMillis();
    if (retryAfterMs > maxRetryAfterMs) {
      return;
    }

    try {
      TimeUnit.MILLISECONDS.sleep(retryAfterMs);
    } catch (InterruptedException e) {
      log.error("Interrupted while waiting for a retry");
      Thread.currentThread().interrupt();
    }
  }
}
