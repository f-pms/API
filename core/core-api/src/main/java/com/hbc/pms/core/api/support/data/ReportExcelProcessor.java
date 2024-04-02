package com.hbc.pms.core.api.support.data;

import static com.hbc.pms.core.api.util.DateTimeUtil.REPORT_DATE_TIME_FORMATTER;
import static com.hbc.pms.core.api.util.DateTimeUtil.convertOffsetDateTimeToLocalDateTime;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_1_PERIOD_1_END_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_1_PERIOD_1_START_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_1_PERIOD_2_END_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_1_PERIOD_2_START_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_1_PERIOD_3_END_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_1_PERIOD_3_START_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_1_PERIOD_4_END_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_1_PERIOD_4_START_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_2_PERIOD_1_END_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_2_PERIOD_1_START_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_2_PERIOD_2_END_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_2_PERIOD_2_START_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_2_PERIOD_3_END_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_2_PERIOD_3_START_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_2_PERIOD_4_END_TIME;
import static com.hbc.pms.core.api.util.ElectricTimeUtil.SHIFT_2_PERIOD_4_START_TIME;
import static java.util.Objects.isNull;

import com.hbc.pms.core.api.support.error.ReportExcelProcessorException;
import com.hbc.pms.core.api.util.ElectricTimeUtil;
import com.hbc.pms.core.model.Report;
import com.hbc.pms.core.model.ReportRow;
import com.hbc.pms.core.model.ReportType;
import com.hbc.pms.core.model.enums.ReportRowShift;
import io.vavr.control.Try;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.util.Precision;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReportExcelProcessor {
  private static final String TEMPLATE_DIR_PATH = "excel-templates";
  private static final Pattern INDICATOR_PATTERN = Pattern.compile("\\((?<indicator>.*)\\)");
  private static final String EXCEL_FILE = "%s.xlsx";

  private static final String SUM_PREFIX = "SUM_";
  private static final String SUM_SPECIFIC_PREFIX = SUM_PREFIX + "SPECIFIC_";
  private static final String IGNORE_POSTFIX = "__";

  @Value("${hbc.report.dir}")
  private String reportDir;

  public XSSFWorkbook cloneWorkbook(String alias) throws IOException, InvalidFormatException {
    var filename = String.format(EXCEL_FILE, alias);
    var templatePath = new ClassPathResource(Paths.get(TEMPLATE_DIR_PATH, filename).toString());
    var templateFile = templatePath.getInputStream();
    var tmpPath = Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID() + filename);

    try {
      FileUtils.copyInputStreamToFile(templateFile, tmpPath.toFile());
    } finally {
      IOUtils.closeQuietly(templateFile);
    }
    return new XSSFWorkbook(tmpPath.toFile());
  }

  public Map<String, CellAddress> getIndicatorsMap(Context context) {
    var indicators = new HashMap<String, CellAddress>();
    var comments = context.getSheet().getCellComments();
    comments.forEach(
        (address, comment) -> {
          var content = comment.getString().toString();
          var matcher = INDICATOR_PATTERN.matcher(content);
          if (!matcher.matches()) {
            return;
          }
          var indicator = matcher.group("indicator");
          if (indicators.containsKey(indicator)) {
            log.error("Duplicate indicator={} at address={}", indicator, address.formatAsString());
            throw new ReportExcelProcessorException("Duplicate indicator in Excel template");
          }
          indicators.put(indicator, address);
        });
    return indicators;
  }

  public Map<String, Double> processSumsMap(Context context, Map<String, CellAddress> indicators) {
    var localDateTime = convertOffsetDateTimeToLocalDateTime(context.getRecordingDate());
    var dayOfWeek = localDateTime.getDayOfWeek();

    context.rows.forEach(row -> fillBaseValue(context, indicators, dayOfWeek, row));

    // force revaluate all formulas
    XSSFFormulaEvaluator.evaluateAllFormulaCells(context.getWorkbook());

    return getSumsMap(context.getSheet(), indicators);
  }

  public void resetDevelopmentCells(Context context, Map<String, CellAddress> indicators) {
    indicators.forEach(
        (indicator, address) -> {
          clearComment(context.getSheet(), address);
          if (indicator.startsWith(SUM_SPECIFIC_PREFIX)) {
            resetCell(context.getSheet(), address);
          }
        });
  }

  public void save(XSSFWorkbook workbook, String dir, String name) {
    try {
      var dirPath = Paths.get(reportDir, dir);
      if (!dirPath.toFile().exists()) {
        dirPath.toFile().mkdir();
      }
      var excelPath = dirPath.resolve(name);
      var fos = new FileOutputStream(excelPath.toString());
      workbook.write(fos);
      fos.close();
      workbook.close();
    } catch (Exception ex) {
      throw new ReportExcelProcessorException("Failed to save the workbook", ex);
    }
  }

  public void close(XSSFWorkbook workbook) {
    Try.run(workbook::close);
  }

  public String getFileName(Report report) {
    return String.format(
        EXCEL_FILE,
        REPORT_DATE_TIME_FORMATTER.format(
            convertOffsetDateTimeToLocalDateTime(report.getRecordingDate())));
  }

  public List<Map<String, Double>> process(ReportType type, Report report, List<ReportRow> rows)
      throws IOException, InvalidFormatException {
    var workbook = cloneWorkbook(type.getName());

    var shift1Rows = rows.stream().filter(r -> r.getShift().equals(ReportRowShift.I)).toList();
    var shift2Rows = rows.stream().filter(r -> r.getShift().equals(ReportRowShift.II)).toList();

    var context1 =
        Context.builder()
            .workbook(workbook)
            .shift(ReportRowShift.I)
            .recordingDate(report.getRecordingDate())
            .rows(shift1Rows)
            .build();
    var context2 =
        Context.builder()
            .workbook(workbook)
            .shift(ReportRowShift.II)
            .recordingDate(report.getRecordingDate())
            .rows(shift2Rows)
            .build();

    var indicator1 = getIndicatorsMap(context1);
    var indicator2 = getIndicatorsMap(context2);

    var sums1 = processSumsMap(context1, indicator1);
    var sums2 = processSumsMap(context2, indicator2);

    resetDevelopmentCells(context1, indicator1);
    resetDevelopmentCells(context2, indicator2);

    save(workbook, type.getName(), getFileName(report));
    return List.of(sums1, sums2);
  }

  private void fillBaseValue(
      Context context, Map<String, CellAddress> indicators, DayOfWeek dayOfWeek, ReportRow row) {
    var indicator = row.getIndicator();

    fill(
        context.getSheet(),
        indicators.get(
            indicator
                + "_0_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek,
                    context.isShift1()
                        ? SHIFT_1_PERIOD_1_START_TIME
                        : SHIFT_2_PERIOD_1_START_TIME)),
        row.getOldElectricValue());
    fill(
        context.getSheet(),
        indicators.get(
            indicator
                + "_1_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek,
                    context.isShift1() ? SHIFT_1_PERIOD_1_END_TIME : SHIFT_2_PERIOD_1_END_TIME)),
        row.getNewElectricValue1());

    fill(
        context.getSheet(),
        indicators.get(
            indicator
                + "_2_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek,
                    context.isShift1()
                        ? SHIFT_1_PERIOD_2_START_TIME
                        : SHIFT_2_PERIOD_2_START_TIME)),
        row.getNewElectricValue1());
    fill(
        context.getSheet(),
        indicators.get(
            indicator
                + "_3_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek,
                    context.isShift1() ? SHIFT_1_PERIOD_2_END_TIME : SHIFT_2_PERIOD_2_END_TIME)),
        row.getNewElectricValue2());

    fill(
        context.getSheet(),
        indicators.get(
            indicator
                + "_4_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek,
                    context.isShift1()
                        ? SHIFT_1_PERIOD_3_START_TIME
                        : SHIFT_2_PERIOD_3_START_TIME)),
        row.getNewElectricValue2());
    fill(
        context.getSheet(),
        indicators.get(
            indicator
                + "_5_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek,
                    context.isShift1() ? SHIFT_1_PERIOD_3_END_TIME : SHIFT_2_PERIOD_3_END_TIME)),
        row.getNewElectricValue3());

    fill(
        context.getSheet(),
        indicators.get(
            indicator
                + "_6_"
                + ElectricTimeUtil.getTimeGroup(
                    context.isShift1() ? dayOfWeek : dayOfWeek.plus(1),
                    context.isShift1()
                        ? SHIFT_1_PERIOD_4_START_TIME
                        : SHIFT_2_PERIOD_4_START_TIME)),
        row.getNewElectricValue3());
    fill(
        context.getSheet(),
        indicators.get(
            indicator
                + "_7_"
                + ElectricTimeUtil.getTimeGroup(
                    context.isShift1() ? dayOfWeek : dayOfWeek.plus(1),
                    context.isShift1() ? SHIFT_1_PERIOD_4_END_TIME : SHIFT_2_PERIOD_4_END_TIME)),
        row.getNewElectricValue4());
  }

  private void fill(XSSFSheet sheet, CellAddress address, Double value) {
    try {
      if (isNull(address)) {
        return;
      }
      var cell = sheet.getRow(address.getRow()).getCell(address.getColumn());
      cell.setCellValue(value);
    } catch (Exception ex) {
      log.warn("Failed to fill data at cell={}", address.formatAsString());
    }
  }

  private Map<String, Double> getSumsMap(XSSFSheet sheet, Map<String, CellAddress> indicators) {
    var sumsMap = new HashMap<String, Double>();
    indicators.forEach(
        (indicator, address) -> {
          if (!indicator.startsWith(SUM_PREFIX) || indicator.endsWith(IGNORE_POSTFIX)) {
            return;
          }

          var oValue = getCellValue(sheet, address);
          if (oValue.isEmpty()) {
            return;
          }
          sumsMap.put(indicator, oValue.get());
        });
    return sumsMap;
  }

  private Optional<Double> getCellValue(XSSFSheet sheet, CellAddress address) {
    final var DEFAULT_SCALE = 3;
    var cell = sheet.getRow(address.getRow()).getCell(address.getColumn());
    switch (cell.getCellType()) {
      case NUMERIC -> {
        return Optional.of(Precision.round(cell.getNumericCellValue(), DEFAULT_SCALE));
      }
      case FORMULA -> {
        if (cell.getCachedFormulaResultType() == CellType.NUMERIC) {
          return Optional.of(Precision.round(cell.getNumericCellValue(), DEFAULT_SCALE));
        }
      }
    }
    return Optional.empty();
  }

  private void clearComment(XSSFSheet sheet, CellAddress address) {
    var cell = sheet.getRow(address.getRow()).getCell(address.getColumn());
    cell.removeCellComment();
  }

  private void resetCell(XSSFSheet sheet, CellAddress address) {
    var cell = sheet.getRow(address.getRow()).getCell(address.getColumn());
    cell.setCellType(CellType.BLANK);
  }

  @Builder
  @Getter
  public static class Context {
    private XSSFWorkbook workbook;
    private ReportRowShift shift;
    private OffsetDateTime recordingDate;
    private List<ReportRow> rows;

    public XSSFSheet getSheet() {
      return workbook.getSheetAt(isShift1() ? 0 : 1);
    }

    public boolean isShift1() {
      return shift.equals(ReportRowShift.I);
    }

    public boolean isShift2() {
      return shift.equals(ReportRowShift.II);
    }
  }
}
