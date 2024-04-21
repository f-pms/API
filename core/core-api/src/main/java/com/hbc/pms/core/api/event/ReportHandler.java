package com.hbc.pms.core.api.event;

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
import java.time.OffsetDateTime;
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

    Try.run(() -> plcConnector.write(checkerAddress.get(), new PlcBOOL(false)));

    var schedules = reportSchedulePersistenceService.getAll();
    var types = schedules.stream().collect(groupingBy(ReportSchedule::getType));
    types.forEach(
        (type, schedulesOfType) -> {
          try {
            var recordingDate = OffsetDateTime.now().minusDays(1); // it must be yesterday

            // save to database
            var report = reportService.createReportByType(type, recordingDate);
            var rows = reportService.createReportRows(response, report.getId(), schedulesOfType);

            // calculate sum
            var reportResult = processor.process(type, report, rows);
            reportService.updateSumJson(report, reportResult.getSums());
            reportService.updateFactorJson(report, reportResult.getFactors());
          } catch (Exception ex) {
            log.error("Failed to process daily report for type={}: {}", type, ex.getMessage());
          }
        });
  }
}
