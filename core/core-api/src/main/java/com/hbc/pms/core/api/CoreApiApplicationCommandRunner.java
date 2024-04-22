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
  public static final String GENERATE_ALL_JSONS = "GENERATE_ALL_JSONS";
  private final ApplicationContext appContext;
  private final ReportGenerationService reportGenerationService;

  @Override
  public void run(String... args) {
    try {
      if (args.length > 0) {
        if (Arrays.asList(args).contains(MISSING_REPORT_JOB)) {
          log.info("Generating missing report");
          reportGenerationService.generateMissingJsons();
        }
        if (Arrays.asList(args).contains(GENERATE_ALL_JSONS)) {
          reportGenerationService.generateAllJsons();
        }
      }
    } catch (Exception e) {
      log.error("Error while running command line runner", e);
    }
    System.exit(SpringApplication.exit(appContext));
  }
}
