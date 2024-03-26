package com.hbc.pms.plc.integration.plc4x.scraper;

import com.hbc.pms.plc.api.exceptions.NotSupportedPlcResponseException;
import com.hbc.pms.plc.api.scraper.HandlerContext;
import com.hbc.pms.plc.api.scraper.ResultHandler;
import com.hbc.pms.plc.integration.plc4x.PlcUtil;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.plc4x.java.scraper.ScraperTask;
import org.apache.plc4x.java.scraper.exception.ScraperException;
import org.apache.plc4x.java.scraper.triggeredscraper.TriggeredScraperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbcScraperTask implements ScraperTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(HbcScraperTask.class);

  private final PlcConnectionManager connectionManager;
  private final String jobName;
  private final String connectionAlias;
  private final String connectionString;
  private final Map<String, String> tags;
  private final long requestTimeoutMs;
  private final ExecutorService executorService;
  private final ResultHandler resultHandler;

  private final AtomicLong requestCounter = new AtomicLong(0);
  private final AtomicLong successCounter = new AtomicLong(0);
  private final DescriptiveStatistics latencyStatistics = new DescriptiveStatistics(1000);
  private final DescriptiveStatistics failedStatistics = new DescriptiveStatistics(1000);

  public HbcScraperTask(
      PlcConnectionManager connectionManager,
      String jobName,
      String connectionAlias,
      String connectionString,
      Map<String, String> tags,
      long requestTimeoutMs,
      ExecutorService executorService,
      ResultHandler resultHandler)
      throws ScraperException {
    this.connectionManager = connectionManager;
    this.jobName = jobName;
    this.connectionAlias = connectionAlias;
    this.connectionString = connectionString;
    this.tags = tags;
    this.requestTimeoutMs = requestTimeoutMs;
    this.executorService = executorService;
    this.resultHandler = resultHandler;
  }

  @Override
  public void run() {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace(
          "Check condition for task of job {} for connection {}", jobName, connectionAlias);
    }
    // Does a single fetch only when trigger is valid
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Trigger for job {} and device {} is met ... scraping desired data",
          jobName,
          connectionAlias);
    }
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace(
          "Start new scrape of task of job {} for connection {}", jobName, connectionAlias);
    }
    requestCounter.incrementAndGet();
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    PlcConnection connection = null;
    try {
      String info = "";
      if (LOGGER.isTraceEnabled()) {
        info =
            String.format(
                "acquiring data collecting connection to (%s,%s)", connectionAlias, jobName);
        LOGGER.trace("acquiring data collecting connection to ({},{})", connectionAlias, jobName);
      }
      connection =
          TriggeredScraperImpl.getPlcConnection(
              connectionManager, connectionString, executorService, requestTimeoutMs, info);
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Connection to {} established: {}", connectionString, connection);
      }

      PlcReadResponse plcReadResponse;
      OffsetDateTime startTime;
      try {
        PlcReadRequest.Builder readRequestBuilder = connection.readRequestBuilder();
        for (Map.Entry<String, String> entry : tags.entrySet()) {
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Requesting: {} -> {}", entry.getKey(), entry.getValue());
          }
          readRequestBuilder.addTagAddress(entry.getKey(), entry.getValue());
        }
        // build and send request and store result in read response
        startTime = OffsetDateTime.now();
        plcReadResponse =
            readRequestBuilder.build().execute().get(requestTimeoutMs, TimeUnit.MILLISECONDS);
      } catch (ExecutionException e) {
        // Handle execution exception
        handleException(e);
        return;
      }

      // Add statistics
      LOGGER.debug("Performing statistics");
      stopWatch.stop();
      latencyStatistics.addValue(stopWatch.getNanoTime());
      failedStatistics.addValue(0.0);
      successCounter.incrementAndGet();
      // Validate response
      validateResponse(plcReadResponse);
      // Handle response (Async)
      CompletableFuture.runAsync(
          () -> {
            try {
              var context = HandlerContext.builder()
                      .jobName(jobName)
                          .alias(connectionAlias)
                              .startTime(startTime)
                                  .build();
              resultHandler.handle(
                  context,
                  PlcUtil.convertPlcResponseToMap(plcReadResponse));
            } catch (NotSupportedPlcResponseException e) {
              throw new RuntimeException(e);
            }
          },
          executorService);
    } catch (Exception e) {
      LOGGER.warn(
          "Exception during scraping of Job {}, Connection-Alias {}: {}",
          jobName,
          connectionAlias,
          e.toString());
      handleException(e);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (Exception e) {
          LOGGER.warn("Error on closing connection", e);
        }
      }
    }
  }

  /**
   * detects if {@link PlcReadResponse} is valid
   *
   * @param response the {@link PlcReadResponse} that should be validated
   */
  private void validateResponse(PlcReadResponse response) {
    Map<String, PlcResponseCode> failedTags =
        response.getTagNames().stream()
            .filter(name -> !PlcResponseCode.OK.equals(response.getResponseCode(name)))
            .collect(Collectors.toMap(Function.identity(), response::getResponseCode));
    if (failedTags.size() > 0) {
      handleErrorResponse(failedTags);
    }
  }

  @Override
  public String getJobName() {
    return this.jobName;
  }

  @Override
  public String getConnectionAlias() {
    return this.connectionAlias;
  }

  @Override
  public long getRequestCounter() {
    return this.requestCounter.get();
  }

  @Override
  public long getSuccessfullRequestCounter() {
    return this.successCounter.get();
  }

  @Override
  public DescriptiveStatistics getLatencyStatistics() {
    return this.latencyStatistics;
  }

  @Override
  public void handleException(Exception e) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Detailed exception occurred at scraping", e);
    }
    failedStatistics.addValue(1.0);
  }

  @Override
  public void handleErrorResponse(Map<String, PlcResponseCode> failed) {
    //    LOGGER.warn("Handling error responses: {}", failed);
  }

  public PlcConnectionManager getConnectionManager() {
    return connectionManager;
  }

  public String getConnectionString() {
    return connectionString;
  }

  public ExecutorService getExecutorService() {
    return executorService;
  }

  public long getRequestTimeoutMs() {
    return requestTimeoutMs;
  }

  @Override
  public String toString() {
    return "TriggeredScraperTask{"
        + "connectionManager="
        + connectionManager
        + ", jobName='"
        + jobName
        + '\''
        + ", connectionAlias='"
        + connectionAlias
        + '\''
        + ", connectionString='"
        + connectionString
        + '\''
        + ", requestTimeoutMs="
        + requestTimeoutMs
        + ", executorService="
        + executorService
        + ", resultHandler="
        + resultHandler
        + '}';
  }

  // ---------------------------------
  // JMX Monitoring
  // ---------------------------------
  public long getScrapesTotal() {
    return requestCounter.get();
  }

  public long getScrapesSuccess() {
    return successCounter.get();
  }

  @Override
  public double getPercentageFailed() {
    return 100.0 - (double) this.getScrapesSuccess() / this.getScrapesTotal() * 100.0;
  }

  public String[] getPercentiles() {
    String[] percentiles = new String[10];
    for (int i = 1; i <= 10; i += 1) {
      percentiles[i - 1] =
          String.format("%d%%: %s ms", 10 * i, latencyStatistics.getPercentile(10.0 * i) * 1e-6);
    }
    return percentiles;
  }
}
