package com.hbc.pms.core.api.event;

import static com.hbc.pms.core.api.constant.PlcConstant.REPORT_JOB_NAME;
import static java.util.stream.Collectors.groupingBy;

import com.hbc.pms.core.api.service.blueprint.BlueprintPersistenceService;
import com.hbc.pms.core.api.service.report.ReportSchedulePersistenceService;
import com.hbc.pms.core.api.service.report.ReportService;
import com.hbc.pms.core.api.support.data.ReportExcelProcessor;
import com.hbc.pms.core.model.ReportSchedule;
import com.hbc.pms.core.model.enums.BlueprintType;
import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.PlcConnector;
import com.hbc.pms.plc.api.scraper.HandlerContext;
import io.vavr.control.Try;
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
            var sums = processor.process(type, report, rows);
            reportService.updateSumJson(report, sums);
          } catch (Exception ex) {
            log.error("Failed to process daily report for type={}: {}", type, ex.getMessage());
          }
        });

    Try.run(() -> plcConnector.write(checkerAddress.get(), new PlcBOOL(false)));
  }
}
