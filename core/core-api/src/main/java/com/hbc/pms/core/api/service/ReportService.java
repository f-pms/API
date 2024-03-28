package com.hbc.pms.core.api.service;

import static java.util.stream.Collectors.groupingBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.pms.core.api.constant.ChartConstant;
import com.hbc.pms.core.api.controller.v1.enums.ChartQueryType;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    return report.getSums();
  }

  public Map<String, Double> getMultiDayChartSummaryFigures(
      SearchMultiDayChartCommand searchCommand) {
    var reportCriteria =
        ReportCriteria.builder()
            .startDate(searchCommand.getStart())
            .endDate(searchCommand.getEnd())
            .build();

    var reports = reportPersistenceService.getAll(reportCriteria);

    return aggregateSumByIndicators(reports, ChartConstant.COMMON_INDICATORS);
  }

  public Map<String, Map<String, List<Double>>> getMultiDayChartFigures(
      SearchMultiDayChartCommand searchCommand) {
    var reportCriteria =
        ReportCriteria.builder()
            .startDate(searchCommand.getStart())
            .endDate(searchCommand.getEnd())
            .build();

    var reports = reportPersistenceService.getAll(reportCriteria);
    var reportsByTypes =
        reports.stream().collect(Collectors.groupingBy(x -> x.getType().getName()));
    var result = new HashMap<String, Map<String, List<Double>>>();

    switch (searchCommand.getChartType()) {
      case PIE:
        for (Map.Entry<String, List<Report>> entry : reportsByTypes.entrySet()) {
          var currentType = entry.getKey();
          var currentReports = entry.getValue();

          result.putIfAbsent(currentType, new HashMap<>());
          var indicatorValuesMap = result.get(currentType);
          var sumByIndicators =
              aggregateSumByIndicators(currentReports, List.of(ChartConstant.SUM_TOTAL));
          indicatorValuesMap.putIfAbsent(
              ChartConstant.SUM_TOTAL, sumByIndicators.values().stream().toList());
        }
        break;
      case MULTI_LINE, STACKED_BAR:
        for (Map.Entry<String, List<Report>> entry : reportsByTypes.entrySet()) {
          var currentType = entry.getKey();
          var currentReports = entry.getValue();
          var indicatorList = currentReports.get(0).getSums().get(0).keySet().stream().toList();

          var reportChunks =
              partitionReportsByTimeUnit(
                  currentReports,
                  searchCommand.getQueryType(),
                  searchCommand.getStart(),
                  searchCommand.getEnd());

          result.putIfAbsent(currentType, new HashMap<>());

          var indicatorValuesMap = result.get(currentType);

          indicatorList.forEach(x -> indicatorValuesMap.putIfAbsent(x, new ArrayList<>()));

          reportChunks.forEach(
              (k, v) -> {
                var aggregatedSum = aggregateSumByIndicators(v, indicatorList);

                indicatorValuesMap.forEach((k3, v3) -> v3.add(aggregatedSum.get(k3)));
              });

          var labels = reportChunks.keySet().stream().toList();
        }
        break;
    }

    return result;
  }

  private Map<String, List<Report>> partitionReportsByTimeUnit(
      List<Report> reports, ChartQueryType queryType, OffsetDateTime start, OffsetDateTime end) {
    var dateTmp = start;

    var partitionsMap = new LinkedHashMap<String, List<Report>>();

    var upperBound = getNextUpperBound(dateTmp, queryType);

    while (upperBound.isBefore(end.plusDays(1))) {
      var partition = new ArrayList<Report>();
      for (Report report : reports) {
        if ((report.getRecordingDate().isAfter(dateTmp)
                || report.getRecordingDate().equals(dateTmp))
            && report.getRecordingDate().isBefore(upperBound)) {
          partition.add(report);
        }
      }

      partitionsMap.put(getLabel(dateTmp, upperBound, queryType), partition);
      dateTmp = upperBound;
      upperBound = getNextUpperBound(dateTmp, queryType);
    }
    return partitionsMap;
  }

  private boolean isBetweenDates(OffsetDateTime date, OffsetDateTime start, OffsetDateTime end) {
    return date.isAfter(start) || date.equals(start) && date.isBefore(end);
  }

  private String getLabel(OffsetDateTime start, OffsetDateTime end, ChartQueryType queryType) {
    return switch (queryType) {
      case DAY, WEEK, MONTH ->
          start.format(DateTimeFormatter.ofPattern("dd/MM"))
              + " - "
              + end.format(DateTimeFormatter.ofPattern("dd/MM"));
      case YEAR ->
          start.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
              + " - "
              + end.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
    };
  }

  private OffsetDateTime getNextUpperBound(OffsetDateTime date, ChartQueryType queryType) {
    return switch (queryType) {
      case DAY -> date.plusDays(1);
      case WEEK -> date.plusWeeks(1);
      case MONTH -> date.plusMonths(1);
      case YEAR -> date.plusYears(1);
    };
  }

  private Double calculateTwoShiftsSum(List<Map<String, Double>> sums, String indicator) {
    return sums.get(0).getOrDefault(indicator, 0.0) + sums.get(1).getOrDefault(indicator, 0.0);
  }

  private Map<String, Double> aggregateSumByIndicators(
      List<Report> reports, List<String> indicators) {
    var result = new HashMap<String, Double>();

    for (Report report : reports) {
      indicators.forEach(
          indic -> {
            result.putIfAbsent(indic, 0.0);
            result.computeIfPresent(
                indic, (key, value) -> value + calculateTwoShiftsSum(report.getSums(), indic));
          });
    }
    return result;
  }
}
