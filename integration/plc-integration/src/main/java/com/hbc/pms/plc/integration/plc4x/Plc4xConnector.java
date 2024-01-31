package com.hbc.pms.plc.integration.plc4x;

import static com.hbc.pms.plc.integration.plc4x.PlcUtil.convertPlcResponseToMap;
import static com.hbc.pms.plc.integration.plc4x.PlcUtil.getIoResponse;

import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.PlcConnector;
import com.hbc.pms.plc.api.ResultHandler;
import com.hbc.pms.plc.api.exceptions.MaximumScraperReachException;
import com.hbc.pms.plc.integration.plc4x.scraper.HbcScraper;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.s7.readwrite.connection.S7HDefaultNettyPlcConnection;
import org.apache.plc4x.java.s7.readwrite.connection.S7HMuxImpl;
import org.apache.plc4x.java.s7.readwrite.tag.S7Tag;
import org.apache.plc4x.java.scraper.Scraper;
import org.apache.plc4x.java.scraper.config.triggeredscraper.ScraperConfigurationTriggeredImpl;
import org.apache.plc4x.java.scraper.config.triggeredscraper.ScraperConfigurationTriggeredImplBuilder;
import org.apache.plc4x.java.scraper.exception.ScraperException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
@Primary
public class Plc4xConnector implements PlcConnector {
  private final ResultHandler resultHandler;
  private final ReentrantLock lock = new ReentrantLock();
  private final AtomicInteger numberOfActiveScraper = new AtomicInteger(0);

  @Value("${hbc.plc.url}")
  private String plcUrl;
  private PlcConnection plcConnection;
  private Scraper scraper;

  public Plc4xConnector(ResultHandler resultHandler) {
    this.resultHandler = resultHandler;
  }

  @SuppressWarnings("java:S1135")
  @PostConstruct
  private void init() throws PlcConnectionException {
    Assert.notNull(plcUrl, "PLC URL must be provided!s");
    plcConnection = PlcDriverManager.getDefault().getConnectionManager().getConnection(plcUrl);
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

  public void updateScheduler(List<String> variableNames) {
    if (scraper != null) {
      scraper.stop();
    }
    if (numberOfActiveScraper.get() > 0) {
      numberOfActiveScraper.decrementAndGet();
    }
    runScheduler(variableNames);
  }

  @SneakyThrows
  public void runScheduler(List<String> variableNames) {
    try {
      lock.lock();
      if (numberOfActiveScraper.get() >= 1) {
        throw new MaximumScraperReachException(
            "Maximum number of active scraper has reached:" + numberOfActiveScraper.get());
      }
      ScraperConfigurationTriggeredImpl scraperConfig = getScraperConfig(variableNames);
      scraper = new HbcScraper(scraperConfig, resultHandler, null);
      scraper.start();
      log.info("Current active scraper: {}", numberOfActiveScraper.incrementAndGet());
    } catch (ScraperException e) {
      log.error("Error starting the scraper", e);
    } finally {
      lock.unlock();
    }
  }

  private ScraperConfigurationTriggeredImpl getScraperConfig(List<String> variableNames) {
    ScraperConfigurationTriggeredImplBuilder scraperConfigBuilder =
        new ScraperConfigurationTriggeredImplBuilder();
    scraperConfigBuilder.addSource("HBC", plcUrl);
    var jobBuilder = scraperConfigBuilder.job("schedule-1", "(SCHEDULED,1000)");
    jobBuilder.source("HBC");
    variableNames.forEach(address -> jobBuilder.tag(address, address));
    jobBuilder.build();
    return scraperConfigBuilder.build();
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
      builder.addTag(address, S7Tag.of(address));
    }
    final PlcReadRequest rr = builder.build();
    PlcReadResponse result = rr.execute().get(4000, TimeUnit.MILLISECONDS);
    stringIoResponseMap.putAll(convertPlcResponseToMap(result));
    return stringIoResponseMap;
  }

  @Override
  @SneakyThrows
  public IoResponse validate(String address) {
    PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
    builder.addTag(address, S7Tag.of(address));
    PlcReadResponse readResponse = builder.build().execute().get();
    return getIoResponse(readResponse, address);
  }
}
