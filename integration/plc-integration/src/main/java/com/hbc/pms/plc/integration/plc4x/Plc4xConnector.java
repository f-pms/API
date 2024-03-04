package com.hbc.pms.plc.integration.plc4x;

import static com.hbc.pms.plc.integration.plc4x.PlcUtil.convertPlcResponseToMap;
import static com.hbc.pms.plc.integration.plc4x.PlcUtil.getIoResponse;

import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.PlcConnector;
import com.hbc.pms.plc.api.exceptions.MaximumScraperReachException;
import com.hbc.pms.plc.api.scraper.ResultHandler;
import com.hbc.pms.plc.api.scraper.ScrapeConfiguration;
import com.hbc.pms.plc.integration.plc4x.scraper.HbcScraper;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.exceptions.PlcInvalidTagException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.s7.readwrite.connection.S7HDefaultNettyPlcConnection;
import org.apache.plc4x.java.s7.readwrite.connection.S7HMuxImpl;
import org.apache.plc4x.java.s7.readwrite.tag.S7Tag;
import org.apache.plc4x.java.scraper.Scraper;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Primary
public class Plc4xConnector implements PlcConnector {

  private final ReentrantLock lock = new ReentrantLock();
  private final AtomicInteger numberOfActiveScraper = new AtomicInteger(0);

  private final ResultHandler resultHandler;
  private final ScrapeConfiguration scrapeConfiguration;
  private final PlcConnectionManager cachedPlcConnectionManager;
  private PlcConnection plcConnection;
  private Scraper scraper;

  public Plc4xConnector(
      ResultHandler resultHandler,
      ScrapeConfiguration scrapeConfiguration,
      PlcConnectionManager cachedPlcConnectionManager) {
    this.resultHandler = resultHandler;
    this.scrapeConfiguration = scrapeConfiguration;
    this.cachedPlcConnectionManager = cachedPlcConnectionManager;
  }

  @SuppressWarnings("java:S1135")
  @PostConstruct
  private void init() throws PlcConnectionException {
    String firstConnectionString =
        scrapeConfiguration.getPlcConfiguration().getDeviceConnections().values().stream()
            .findFirst()
            .orElseThrow();
    plcConnection =
        PlcDriverManager.getDefault().getConnectionManager().getConnection(firstConnectionString);
  }

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    runScheduler();
  }

  @SneakyThrows
  public boolean tryToConnect() {
    if (plcConnection != null && !isConnected()) {
      try {
        log.info("Try to reconnect to PLC");
        plcConnection.close();
        init();
      } catch (PlcConnectionException plcConnectionException) {
        log.error("Failed to connect to PLC", plcConnectionException);
        return false;
      }
    }
    return isConnected();
  }

  public boolean isConnected() {
    if (plcConnection instanceof S7HDefaultNettyPlcConnection s7HDefaultNettyPlcConnection) {
      return s7HDefaultNettyPlcConnection.getChannel().attr(S7HMuxImpl.IS_CONNECTED).get();
    }
    return false;
  }

  public void updateScheduler() {
    if (scraper != null) {
      scraper.stop();
    }
    if (numberOfActiveScraper.get() > 0) {
      numberOfActiveScraper.decrementAndGet();
    }
    runScheduler();
  }

  @SneakyThrows
  public void runScheduler() {
    try {
      lock.lock();
      if (numberOfActiveScraper.get() >= 1) {
        throw new MaximumScraperReachException(
            "Maximum number of active scraper has reached:" + numberOfActiveScraper.get());
      }
      scraper =
          new HbcScraper(resultHandler, scrapeConfiguration.getJobs(), cachedPlcConnectionManager);
      scraper.start();
      log.info("Current active scraper: {}", numberOfActiveScraper.incrementAndGet());
    } finally {
      lock.unlock();
    }
  }

  @Override
  @SneakyThrows
  public Map<String, IoResponse> executeBlockRequest(List<String> variableNames) {
    if (!tryToConnect()) {
      return Map.of();
    }
    Map<String, IoResponse> stringIoResponseMap = new HashMap<>();
    if (!plcConnection.getMetadata().canRead()) {
      log.error("This connection doesn't support reading.");
      return stringIoResponseMap;
    }
    PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
    for (var address : variableNames) {
      try {
        builder.addTag(address, S7Tag.of(address));
      } catch (PlcInvalidTagException e) {
        log.error(e.getMessage());
      }
    }
    final PlcReadRequest rr = builder.build();
    PlcReadResponse result = rr.execute().get(4000, TimeUnit.MILLISECONDS);
    stringIoResponseMap.putAll(convertPlcResponseToMap(result));
    return stringIoResponseMap;
  }

  @Override
  public IoResponse validate(String address) throws ExecutionException, InterruptedException {
    PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
    builder.addTag(address, S7Tag.of(address));
    PlcReadResponse readResponse = builder.build().execute().get();
    return getIoResponse(readResponse, address);
  }
}
