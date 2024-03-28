package com.hbc.pms.core.api.service;

import static java.util.stream.Collectors.groupingBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.pms.core.api.constant.ChartConstant;
import com.hbc.pms.core.api.controller.v1.request.SearchMultiDayChartCommand;
import com.hbc.pms.core.model.Report;
import com.hbc.pms.core.model.ReportRow;
import com.hbc.pms.core.model.ReportSchedule;
import com.hbc.pms.core.model.ReportType;
import com.hbc.pms.core.model.criteria.ReportCriteria;
import com.hbc.pms.core.model.enums.ReportRowPeriod;
import com.hbc.pms.plc.api.IoResponse;
import io.vavr.control.Try;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
  private final ReportPersistenceService reportPersistenceService;
  private final ReportRowPersistenceService reportRowPersistenceService;
  private final ReportTypePersistenceService reportTypePersistenceService;

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

  public List<Map<String, Double>> getOneDayChartFigures(Long reportId) {
    var report = reportPersistenceService.getById(reportId);
    var sumJson = report.getSums();
    return sumJson.stream()
        .map(
            x ->
                x.entrySet().stream()
                    .filter(
                        e ->
                            ChartConstant.REPORT_TYPE_TO_KEYS
                                .get(report.getType().getId())
                                .contains(e.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
        .toList();
  }

  public Map<String, Double> getMultiDaySummaryChartFigures(
      SearchMultiDayChartCommand searchCommand) {
    var reportCriteria =
        ReportCriteria.builder()
            .startDate(searchCommand.getStart().toInstant().atOffset(ZoneOffset.UTC))
            .endDate(searchCommand.getEnd().toInstant().atOffset(ZoneOffset.UTC))
            .build();

    var reports = reportPersistenceService.getAll(reportCriteria);

    var result =
        ChartConstant.COMMON_KEYS_LIST.stream()
            .map(key -> Map.entry(key, 0.0))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    for (Report report : reports) {
      var sumJson = report.getSums();

      for (Map.Entry<String, Double> entry : result.entrySet()) {
        var key = entry.getKey();
        var value = entry.getValue();
        var twoShiftSum = sumJson.get(0).get(key) + sumJson.get(1).get(key);

        entry.setValue(value + twoShiftSum);
      }
    }

    return calculateTwoShiftsSumsByKeys(reports, ChartConstant.COMMON_KEYS_LIST);
  }

  private Map<String, Double> calculateTwoShiftsSumsByKeys(
      List<Report> reports, List<String> keys) {
    Map<String, Double> result =
        keys.stream()
            .map(key -> Map.entry(key, 0.0))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    for (Report report : reports) {
      var sumJson = report.getSums();

      for (Map.Entry<String, Double> entry : result.entrySet()) {
        var key = entry.getKey();
        var value = entry.getValue();
        var twoShiftSum = sumJson.get(0).get(key) + sumJson.get(1).get(key);

        entry.setValue(value + twoShiftSum);
      }
    }

    return result;
  }

  public Map<String, Map<String, List<Double>>> getMultiDayChartFigures(
      SearchMultiDayChartCommand searchCommand) {
    var reportCriteria =
        ReportCriteria.builder()
            .startDate(searchCommand.getStart().toInstant().atOffset(ZoneOffset.UTC))
            .endDate(searchCommand.getEnd().toInstant().atOffset(ZoneOffset.UTC))
            .build();

    var reports = reportPersistenceService.getAll(reportCriteria);
    var reportTypes = reportTypePersistenceService.getAll();
    Map<String, Map<String, List<Double>>> result =
        reportTypes.stream()
            .map(type -> Map.entry(type.getName(), new HashMap<String, List<Double>>()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    switch (searchCommand.getChartType()) {
      case PIE:
        for (ReportType type : reportTypes) {
          var reportsByType =
              reports.stream().filter(x -> x.getType().getId().equals(type.getId())).toList();
          var sum = calculateTwoShiftsSumsByKeys(reportsByType, List.of(ChartConstant.SUM_TOTAL));
          result.put(
              type.getName(),
              sum.entrySet().stream()
                  .collect(Collectors.toMap(Map.Entry::getKey, e -> List.of(e.getValue()))));
        }
        break;
      case MULTI_LINE:
        for (ReportType type : reportTypes) {
          var rangeStart = searchCommand.getStart().toInstant().atOffset(ZoneOffset.UTC);
          var rangeEnd = rangeStart.plusDays(1);
//          var reportsByType =
//              reports.stream().filter(x -> x.getType().getId().equals(type.getId())).toList();
          var indicatorsList = List.of(ChartConstant.SUM_TOTAL);

          while (rangeEnd.isBefore(searchCommand.getEnd().toInstant().atOffset(ZoneOffset.UTC))) {
            var reportCriteria2 =
                ReportCriteria.builder()
                    .startDate(searchCommand.getStart().toInstant().atOffset(ZoneOffset.UTC))
                    .endDate(searchCommand.getEnd().toInstant().atOffset(ZoneOffset.UTC))
                    .typeIds(List.of(type.getId()))
                    .build();



            var reportsByDate = reportPersistenceService.getAll(reportCriteria2);
//            var reportsByDate =
//                reportsByType.stream()
//                    .filter(
//                        x ->
//                            x.getRecordingDate().isAfter(rangeStart)
//                                || x.getRecordingDate().equals(rangeStart)
//                                    && x.getRecordingDate().isBefore(rangeEnd))
//                    .toList();
            var sum = calculateTwoShiftsSumsByKeys(reportsByDate, List.of(ChartConstant.SUM_TOTAL));

            for (String indic : indicatorsList) {
              var valuesList = result.get(type.getName()).get(indic);
              if (valuesList == null) {
                valuesList = new ArrayList<>();
              }
              valuesList.add(sum.get(indic));
              result.get(type.getName()).put(indic, valuesList);
            }
            rangeEnd = rangeEnd.plusDays(1);
          }
        }
        break;
      case STACKED_BAR:
        return null;
    }
    return result;
  }
}
