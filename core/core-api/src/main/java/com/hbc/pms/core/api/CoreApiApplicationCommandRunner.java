package com.hbc.pms.core.api;

import com.hbc.pms.core.api.service.report.ReportGenerationService;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "job", havingValue = "commandLineRunner")
public class CoreApiApplicationCommandRunner implements CommandLineRunner {
  public static final String MISSING_REPORT_JOB = "MISSING_REPORT_JOB";
  private final ApplicationContext appContext;
  private final ReportGenerationService reportGenerationService;

  @Override
  public void run(String... args) {
    try {
      if (args.length > 0 && Arrays.asList(args).contains(MISSING_REPORT_JOB)) {
        generateMissingReport();
      }
    } catch (Exception e) {
      log.error("Error while running command line runner", e);
    }
    System.exit(SpringApplication.exit(appContext));
  }

  private void generateMissingReport() {
    log.info("Generating missing report");
    reportGenerationService.generateMissingSumJson();
  }
}
