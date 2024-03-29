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
      case PIE:
        reportsByTypes.forEach(
            (reportType, currentReports) -> {
              chartData.putIfAbsent(reportType, new HashMap<>());
              var indicatorValuesMap = chartData.get(reportType);
              var sumByIndicators =
                  aggregateSumByIndicators(currentReports, List.of(ChartConstant.SUM_TOTAL));
              indicatorValuesMap.putIfAbsent(
                  ChartConstant.SUM_TOTAL, sumByIndicators.values().stream().toList());
            });
        break;
      case MULTI_LINE, STACKED_BAR:
        reportsByTypes.forEach(
            (reportType, currentReports) -> {
              var indicatorList = currentReports.get(0).getSums().get(0).keySet().stream().toList();

              var reportChunks =
                  partitionReportsByTimeUnit(
                      currentReports,
                      searchCommand.getQueryType(),
                      searchCommand.getStart(),
                      searchCommand.getEnd());

              chartData.putIfAbsent(reportType, new HashMap<>());

              var indicatorValuesMap = chartData.get(reportType);

              indicatorList.forEach(x -> indicatorValuesMap.putIfAbsent(x, new ArrayList<>()));

              reportChunks.forEach(
                  (label, reportsList) -> {
                    var aggregatedSum = aggregateSumByIndicators(reportsList, indicatorList);

                    indicatorValuesMap.forEach(
                        (indicator, summedList) -> summedList.add(aggregatedSum.get(indicator)));
                  });

              var labels = reportChunks.keySet().stream().toList();
              result.setLabelSteps(labels);
            });
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + searchCommand.getChartType());
    }

    result.setData(chartData);

    return result;
  }

  private Map<String, List<Report>> groupReportsByType(List<Report> reports) {
    return reports.stream().collect(groupingBy(report -> report.getType().getName()));
  }

  private Map<String, List<Report>> partitionReportsByTimeUnit(
      List<Report> reports, ChartQueryType queryType, OffsetDateTime start, OffsetDateTime end) {
    var currentDate = start;

    var partitionsMap = new LinkedHashMap<String, List<Report>>();

    var upperBound = getNextUpperBoundDate(currentDate, queryType);

    while (upperBound.isBefore(end.plusDays(1))) {
      List<Report> partition = filterReportsByTimePeriod(reports, currentDate, upperBound);
      String label = getDateRangeLabel(currentDate, upperBound, queryType);
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
