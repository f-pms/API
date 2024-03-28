package com.hbc.pms.core.api.service;

import static com.hbc.pms.core.api.constant.ReportConstant.EXCEL_FILE;
import static com.hbc.pms.core.api.util.DateTimeUtil.REPORT_DATE_TIME_FORMATTER;
import static com.hbc.pms.core.api.util.DateTimeUtil.convertOffsetDateTimeToLocalDateTime;
import static java.util.Objects.isNull;


import com.hbc.pms.core.api.service.report.ReportPersistenceService;
import com.hbc.pms.core.api.support.data.ReportExcelProcessor;
import com.hbc.pms.core.model.Report;
import com.hbc.pms.core.model.criteria.ReportCriteria;
import com.hbc.pms.core.model.enums.ReportRowShift;
import io.vavr.control.Try;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportDownloaderService {
  private final ReportPersistenceService reportPersistenceService;
  private final ReportExcelProcessor processor;
  private final Executor executor = Executors.newFixedThreadPool(5);

  @Value("${hbc.report.dir}")
  private String reportDir;

  public List<String> downloadByIds(List<Long> ids) {
    var criteria = ReportCriteria.builder().ids(ids).build();
    return process(criteria);
  }

  public List<String> downloadByDate(OffsetDateTime startDate, OffsetDateTime endDate) {
    var criteria = ReportCriteria.builder().startDate(startDate).endDate(endDate).build();
    return process(criteria);
  }

  private List<String> process(ReportCriteria criteria) {
    var reports = reportPersistenceService.getAll(criteria);
    var missingReports = reports.stream().filter(this::notFoundPredicate).toList();
    generateReports(missingReports);
    return reports.stream()
        .map(report -> report.getType().getName() + "/" + this.getFileName(report))
        .toList();
  }

  private boolean notFoundPredicate(Report report) {
    var alias = report.getType().getName();
    return !Paths.get(reportDir, alias, getFileName(report)).toFile().exists();
  }

  private void generateReports(List<Report> reports) {
    var futures = new ArrayList<CompletableFuture<?>>();
    reports.forEach(
        report ->
            futures.add(
                CompletableFuture.runAsync(() -> generateReport(report.getId()), executor)));
    CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join();
  }

  private void generateReport(Long id) {
    var report = reportPersistenceService.getByIdWithRows(id);
    var alias = report.getType().getName();
    var rows = report.getRows();

    var workbook = Try.of(() -> processor.cloneWorkbook(report.getType().getName())).getOrNull();
    if (isNull(workbook)) {
      return;
    }

    var shift1Rows = rows.stream().filter(r -> r.getShift().equals(ReportRowShift.I)).toList();
    var shift2Rows = rows.stream().filter(r -> r.getShift().equals(ReportRowShift.II)).toList();
    var context1 =
        ReportExcelProcessor.Context.builder()
            .workbook(workbook)
            .shift(ReportRowShift.I)
            .rows(shift1Rows)
            .build();
    var context2 =
        ReportExcelProcessor.Context.builder()
            .workbook(workbook)
            .shift(ReportRowShift.II)
            .rows(shift2Rows)
            .build();
    var indicator1 = processor.getIndicatorsMap(context1);
    var indicator2 = processor.getIndicatorsMap(context2);
    processor.processSumsMap(context1, indicator1, report.getRecordingDate());
    processor.processSumsMap(context2, indicator2, report.getRecordingDate());
    processor.resetDevelopmentCells(context1, indicator1);
    processor.resetDevelopmentCells(context2, indicator2);
    processor.save(workbook, alias, getFileName(report));
  }

  private String getFileName(Report report) {
    return String.format(
        EXCEL_FILE,
        REPORT_DATE_TIME_FORMATTER.format(
            convertOffsetDateTimeToLocalDateTime(report.getRecordingDate())));
  }
}
