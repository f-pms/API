package com.hbc.pms.core.api.event;

import static com.hbc.pms.core.api.constant.PlcConstant.REPORT_JOB_NAME;
import static java.util.stream.Collectors.groupingBy;

import com.hbc.pms.core.api.service.BlueprintPersistenceService;
import com.hbc.pms.core.api.service.ReportSchedulePersistenceService;
import com.hbc.pms.core.api.service.ReportService;
import com.hbc.pms.core.api.support.data.ReportExcelProcessor;
import com.hbc.pms.core.model.ReportSchedule;
import com.hbc.pms.core.model.enums.BlueprintType;
import com.hbc.pms.core.model.enums.ReportRowShift;
import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.PlcConnector;
import com.hbc.pms.plc.api.scraper.HandlerContext;
import io.vavr.control.Try;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.plc4x.java.spi.values.PlcBOOL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportHandler implements RmsHandler {
  private final String BOOL_PATTERN = ":BOOL";
  private final ReportSchedulePersistenceService reportSchedulePersistenceService;
  private final ReportService reportService;
  private final ReportExcelProcessor processor;
  private final BlueprintPersistenceService blueprintService;

  @Lazy @Autowired private PlcConnector plcConnector; // fix circular dependency

  @Override
  public void handle(HandlerContext context, Map<String, IoResponse> response) {
    if (!context.getJobName().equals(REPORT_JOB_NAME)) {
      return;
    }

    var reportAddresses =
        blueprintService.getAll().stream()
            .filter(blueprint -> blueprint.getType().equals(BlueprintType.REPORT))
            .flatMap(blueprint -> blueprint.getAddresses().stream())
            .toList();

    var checkerAddress =
        reportAddresses.stream().filter(address -> address.endsWith(BOOL_PATTERN)).findFirst();
    if (checkerAddress.isEmpty()
        || !response.get(checkerAddress.get()).getPlcValue().getBoolean()) {
      return;
    }

    var schedules = reportSchedulePersistenceService.getAll();
    var types = schedules.stream().collect(groupingBy(ReportSchedule::getType));
    types.forEach(
        (type, schedulesOfType) -> {
          try {
            // save to database
            var report = reportService.createReportByType(type);
            var rows = reportService.createReportRows(response, report.getId(), schedulesOfType);

            // calculate sum
            var workbook = processor.cloneWorkbook(type.getName());
            var shift1Rows =
                rows.stream().filter(r -> r.getShift().equals(ReportRowShift.I)).toList();
            var shift2Rows =
                rows.stream().filter(r -> r.getShift().equals(ReportRowShift.II)).toList();
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
            var sums1 = processor.processSumsMap(context1, indicator1, OffsetDateTime.now());
            var sums2 = processor.processSumsMap(context2, indicator2, OffsetDateTime.now());
            processor.close(workbook);
            reportService.updateSumJson(report, List.of(sums1, sums2));
          } catch (Exception ex) {
            log.error("Failed to process daily report for type={}: {}", type, ex.getMessage());
          }
        });

    Try.run(() -> plcConnector.write(checkerAddress.get(), new PlcBOOL(false)));
  }
}
