package com.hbc.pms.core.api.service.report;

import com.hbc.pms.core.api.config.report.ReportConfiguration;
import com.hbc.pms.core.api.support.data.ReportExcelProcessor;
import com.hbc.pms.core.model.Report;
import io.vavr.control.Try;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportGenerationService {
  private final ReportPersistenceService reportPersistenceService;
  private final ReportExcelProcessor reportExcelProcessor;
  private final ReportConfiguration reportConfiguration;
  private final ReportService reportService;

  private final Executor executor = Executors.newFixedThreadPool(5);

  public void generateMissingSumJson() {
    var reports = reportPersistenceService.getAllEmptySumJson();
    final int totalReports = reports.size();
    log.info("Starting to process {} reports.", totalReports);
    AtomicInteger processedCount = new AtomicInteger();
    ScheduledExecutorService progressLogger = Executors.newSingleThreadScheduledExecutor();
    progressLogger.scheduleAtFixedRate(
        () -> {
          int processed = processedCount.get();
          log.info("Processed {}/{} reports.", processed, totalReports);
        },
        5,
        5,
        TimeUnit.SECONDS);
    var futures =
        reports.stream()
            .map(
                report ->
                    CompletableFuture.runAsync(
                        () -> {
                          generateSumJson(report);
                          processedCount.incrementAndGet();
                        },
                        executor))
            .toList();
    CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
  }

  public void generateAllReportFiles() {
    generateReports(reportPersistenceService.getAll().stream().map(Report::getId).toList());
  }

  @SneakyThrows
  public void generateSumJson(Report report) {
    var sums = reportExcelProcessor.process(report.getType(), report, report.getRows());
    log.debug("Saved excel file for report: {}", report.getId());
    var reportWithoutRows =
        reportPersistenceService.getById(
            report.getId()); // workaround to avoid changing existing code
    reportService.updateSumJson(reportWithoutRows, sums);
    log.debug("Update sumJson for report: {}", report.getId());
  }

  public void generateMissingExcelFiles() {
    var reports = reportPersistenceService.getAll();
    generateMissingExcelFiles(reports);
  }

  public void generateMissingExcelFiles(Collection<Report> reportsToCheck) {
    var missingReports = reportsToCheck.stream().filter(this::notFoundPredicate).toList();
    generateReports(missingReports.stream().map(Report::getId).toList());
  }

  public void generateReports(List<Long> ids) {
    var futures = ids.stream().map(this::generateReportAsync).toArray(CompletableFuture<?>[]::new);
    CompletableFuture.allOf(futures).join();
  }

  private CompletableFuture<Void> generateReportAsync(Long id) {
    return CompletableFuture.runAsync(() -> generateReport(id), executor);
  }

  private void generateReport(Long id) {
    Try.run(
        () -> {
          var report = reportPersistenceService.getByIdWithRows(id);
          var type = report.getType();
          var rows = report.getRows();
          reportExcelProcessor.process(type, report, rows);
          log.info("Saved excel file for report: {}", report.getId());
        });
  }

  private boolean notFoundPredicate(Report report) {
    var alias = report.getType().getName();
    return !Paths.get(reportConfiguration.getDir(), alias, reportExcelProcessor.getFileName(report))
        .toFile()
        .exists();
  }
}
