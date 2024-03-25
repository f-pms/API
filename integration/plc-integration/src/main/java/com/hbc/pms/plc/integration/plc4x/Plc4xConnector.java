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
import lombok.RequiredArgsConstructor;
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
}
