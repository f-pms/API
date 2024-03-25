package com.hbc.pms.plc.integration.plc4x;

import static com.hbc.pms.plc.api.PlcConstant.DEVICE_NAME;

import com.hbc.pms.plc.api.PlcConnector;
import com.hbc.pms.plc.api.exceptions.MaximumScraperReachException;
import com.hbc.pms.plc.api.exceptions.WritePlcException;
import com.hbc.pms.plc.api.scraper.ResultHandler;
import com.hbc.pms.plc.api.scraper.ScrapeConfiguration;
import com.hbc.pms.plc.integration.plc4x.scraper.HbcScraper;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.plc4x.java.scraper.Scraper;
import org.apache.plc4x.java.spi.values.PlcIECValue;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Primary
@RequiredArgsConstructor
public class Plc4xConnector implements PlcConnector {

  private final ReentrantLock lock = new ReentrantLock();
  private final AtomicInteger numberOfActiveScraper = new AtomicInteger(0);

  private final ResultHandler resultHandler;
  private final ScrapeConfiguration scrapeConfiguration;
  private final PlcConnectionManager cachedPlcConnectionManager;
  private Scraper scraper;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    runScheduler();
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

  public PlcResponseCode write(String address, PlcIECValue<?> value) throws WritePlcException {
    try {
      var connectionUrl =
          scrapeConfiguration.getPlcConfiguration().getDeviceConnections().get(DEVICE_NAME);
      var connection = cachedPlcConnectionManager.getConnection(connectionUrl);
      var writeRequest =
          connection.writeRequestBuilder().addTagAddress(address, address, value).build();
      var response = writeRequest.execute().get();
      return response.getResponseCode(address);
    } catch (Exception ex) {
      throw new WritePlcException(ex.getMessage(), ex.getCause());
    }
  }
}
