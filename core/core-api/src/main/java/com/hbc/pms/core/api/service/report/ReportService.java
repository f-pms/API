package com.hbc.pms.core.api.service.report;

import static java.util.stream.Collectors.groupingBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.pms.core.model.Report;
import com.hbc.pms.core.model.ReportRow;
import com.hbc.pms.core.model.ReportSchedule;
import com.hbc.pms.core.model.ReportType;
import com.hbc.pms.core.model.enums.ReportRowPeriod;
import com.hbc.pms.plc.api.IoResponse;
import io.vavr.control.Try;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
  private final ReportPersistenceService reportPersistenceService;
  private final ReportRowPersistenceService reportRowPersistenceService;

  public Report createReportByType(ReportType type) {
    return reportPersistenceService.create(
        Report.builder()
            .type(ReportType.builder().id(type.getId()).build())
            .recordingDate(OffsetDateTime.now())
            .build());
  }

  public List<ReportRow> createReportRows(
      Map<String, IoResponse> response, Long reportId, List<ReportSchedule> schedulesOfType) {
    var rows = new ArrayList<ReportRow>();
    schedulesOfType.stream()
        .collect(
            groupingBy(
                schedule -> new ImmutablePair<>(schedule.getIndicator(), schedule.getShift())))
        .forEach(
            (indicator, schedulesOfIndicator) -> {
              var rowBuilder = ReportRow.builder().indicator(indicator.left).shift(indicator.right);
              for (ReportRowPeriod period : ReportRowPeriod.values()) {
                createRowForPeriod(response, rowBuilder, schedulesOfIndicator, period);
              }
              var newRow = rowBuilder.build();
              rows.add(reportRowPersistenceService.create(newRow, reportId));
            });
    return rows;
  }

  public void updateSumJson(Report report, List<Map<String, Double>> sums) {
    var mapper = new ObjectMapper();
    report.setSumJson(Try.of(() -> mapper.writeValueAsString(sums)).getOrElse(""));
    reportPersistenceService.update(report.getId(), report);
  }

  private void createRowForPeriod(
      Map<String, IoResponse> response,
      ReportRow.ReportRowBuilder rowBuilder,
      List<ReportSchedule> schedulesOfIndicator,
      ReportRowPeriod period) {
    schedulesOfIndicator.stream()
        .filter(schedule -> schedule.getPeriod().equals(period))
        .findFirst()
        .ifPresent(
            schedule -> {
              var electricValue =
                  response
                      .get(schedule.getSensorConfiguration().getAddress())
                      .getPlcValue()
                      .getDouble();
              switch (period) {
                case OLD -> rowBuilder.oldElectricValue(electricValue);
                case NEW1 -> rowBuilder.newElectricValue1(electricValue);
                case NEW2 -> rowBuilder.newElectricValue2(electricValue);
                case NEW3 -> rowBuilder.newElectricValue3(electricValue);
                case NEW4 -> rowBuilder.newElectricValue4(electricValue);
              }
            });
  }
}
