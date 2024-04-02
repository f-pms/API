package com.hbc.pms.core.api.service.report;

import static com.hbc.pms.core.api.util.DateUtil.getDateRangeLabel;
import static com.hbc.pms.core.api.util.DateUtil.getNextUpperBoundDate;
import static com.hbc.pms.core.api.util.DateUtil.isBetweenDates;
import static java.util.stream.Collectors.groupingBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.pms.core.api.constant.ChartConstant;
import com.hbc.pms.core.api.controller.v1.enums.ChartQueryType;
import com.hbc.pms.core.api.controller.v1.request.SearchMultiDayChartCommand;
import com.hbc.pms.core.api.controller.v1.response.MultiDayChartResponse;
import com.hbc.pms.core.api.controller.v1.response.OneDayChartResponse;
import com.hbc.pms.core.model.Report;
import com.hbc.pms.core.model.ReportRow;
import com.hbc.pms.core.model.ReportSchedule;
import com.hbc.pms.core.model.ReportType;
import com.hbc.pms.core.model.criteria.ReportCriteria;
import com.hbc.pms.core.model.enums.ReportRowPeriod;
import com.hbc.pms.plc.api.IoResponse;
import io.vavr.control.Try;
import java.time.OffsetDateTime;
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

  private static final String SUM_SPECIFIC_PREFIX = "SUM_SPECIFIC_";

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

  public OneDayChartResponse getOneDayChartFigures(Long reportId) {
    var report = reportPersistenceService.getById(reportId);
    return OneDayChartResponse.builder()
        .data(report.getSums())
        .recordingDate(report.getRecordingDate())
        .build();
  }

  public Map<String, Double> getMultiDayChartSummaryFigures(
      SearchMultiDayChartCommand searchCommand) {
    var reportCriteria =
        ReportCriteria.builder()
            .startDate(searchCommand.getStart())
            .endDate(searchCommand.getEnd())
            .build();

    var reports = reportPersistenceService.getAll(reportCriteria).stream().toList();

    return aggregateSumByIndicators(reports, ChartConstant.COMMON_INDICATORS);
  }

  public MultiDayChartResponse getMultiDayChartFigures(SearchMultiDayChartCommand searchCommand) {
    var reportCriteria =
        ReportCriteria.builder()
            .startDate(searchCommand.getStart())
            .endDate(searchCommand.getEnd())
            .build();

    List<Report> reports = reportPersistenceService.getAll(reportCriteria).stream().toList();
    Map<String, List<Report>> reportsByTypes = groupReportsByType(reports);
    var chartData = new HashMap<String, Map<String, List<Double>>>();
    var result = new MultiDayChartResponse();

    switch (searchCommand.getChartType()) {
      case PIE ->
          reportsByTypes.forEach(
              (reportType, currentReports) -> {
                chartData.putIfAbsent(reportType, new HashMap<>());
                var indicatorValuesMap = chartData.get(reportType);
                var sumByIndicators =
                    aggregateSumByIndicators(currentReports, List.of(ChartConstant.SUM_TOTAL));
                indicatorValuesMap.putIfAbsent(
                    ChartConstant.SUM_TOTAL, sumByIndicators.values().stream().toList());
              });
      case MULTI_LINE, STACKED_BAR ->
          reportsByTypes.forEach(
              (reportType, currentReports) -> {
                if (!currentReports.isEmpty()) {
                  var indicators =
                      currentReports.get(0).getSums().get(0).keySet().stream()
                          .filter(
                              indicator ->
                                  ChartConstant.COMMON_INDICATORS.contains(indicator)
                                      || indicator.startsWith(SUM_SPECIFIC_PREFIX))
                          .toList();

                  var reportChunks =
                      partitionReportsByTimeUnit(
                          currentReports,
                          searchCommand.getQueryType(),
                          searchCommand.getStart(),
                          searchCommand.getEnd());

                  chartData.putIfAbsent(reportType, new HashMap<>());

                  var indicatorValuesMap = chartData.get(reportType);

                  indicators.forEach(
                      indicator -> indicatorValuesMap.putIfAbsent(indicator, new ArrayList<>()));

                  reportChunks.forEach(
                      (label, reportsChunk) -> {
                        var aggregatedSum = aggregateSumByIndicators(reportsChunk, indicators);

                        indicatorValuesMap.forEach(
                            (indicator, summedList) ->
                                summedList.add(aggregatedSum.get(indicator)));
                      });

                  var labels = reportChunks.keySet().stream().toList();
                  result.setLabelSteps(labels);
                }
              });
    }

    var missingDatesByType =
        getMissingDatesInReportsGroupByType(
            reportsByTypes, searchCommand.getStart(), searchCommand.getEnd());
    result.setData(chartData);
    result.setMissingDates(missingDatesByType);

    return result;
  }

  private Map<String, List<OffsetDateTime>> getMissingDatesInReportsGroupByType(
      Map<String, List<Report>> reportsByType, OffsetDateTime start, OffsetDateTime end) {
    Map<String, List<OffsetDateTime>> missingDatesByType = new HashMap<>();

    reportsByType.forEach(
        (reportType, currentReports) -> {
          OffsetDateTime currentDate = start;

          var dates = new ArrayList<OffsetDateTime>();
          while (currentDate.isBefore(end)) {
            OffsetDateTime finalCurrentDate = currentDate;
            boolean dateExistsInReports =
                currentReports.stream()
                    .anyMatch(
                        report ->
                            report
                                .getRecordingDate()
                                .toLocalDate()
                                .isEqual(finalCurrentDate.toLocalDate()));

            if (!dateExistsInReports) {
              dates.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
          }

          missingDatesByType.put(reportType, dates);
        });

    return missingDatesByType;
  }

  private Map<String, List<Report>> groupReportsByType(List<Report> reports) {
    if (reports.isEmpty()) {
      var reportTypes = reportTypePersistenceService.getAll();
      return reportTypes.stream()
          .collect(Collectors.toMap(ReportType::getName, reportType -> new ArrayList<>()));
    }

    return reports.stream().collect(groupingBy(report -> report.getType().getName()));
  }

  private Map<String, List<Report>> partitionReportsByTimeUnit(
      List<Report> reports, ChartQueryType queryType, OffsetDateTime start, OffsetDateTime end) {
    var currentDate = start;

    var partitionsMap = new LinkedHashMap<String, List<Report>>();

    var upperBound = getNextUpperBoundDate(currentDate, queryType);

    while (upperBound.isBefore(end.plusDays(1))) {
      List<Report> partition = filterReportsByTimePeriod(reports, currentDate, upperBound);
      String label = getDateRangeLabel(currentDate, upperBound.minusDays(1), queryType);
      partitionsMap.put(label, partition);

      currentDate = upperBound;
      upperBound = getNextUpperBoundDate(currentDate, queryType);
    }
    return partitionsMap;
  }

  private List<Report> filterReportsByTimePeriod(
      List<Report> reports, OffsetDateTime startDate, OffsetDateTime endDate) {
    return reports.stream()
        .filter(report -> isBetweenDates(report.getRecordingDate(), startDate, endDate))
        .toList();
  }

  private Double calculateTwoShiftsSum(List<Map<String, Double>> sums, String indicator) {
    return sums.stream().mapToDouble(sum -> sum.getOrDefault(indicator, 0.0)).sum();
  }

  private Map<String, Double> aggregateSumByIndicators(
      List<Report> reports, List<String> indicators) {

    return reports.stream()
        .flatMap(
            report ->
                indicators.stream()
                    .map(
                        indicator ->
                            Map.entry(
                                indicator, calculateTwoShiftsSum(report.getSums(), indicator))))
        .collect(groupingBy(Map.Entry::getKey, Collectors.summingDouble(Map.Entry::getValue)));
  }
}
