package com.hbc.pms.core.api.support.data;

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

import com.hbc.pms.core.api.util.ElectricTimeUtil;
import com.hbc.pms.core.model.ReportRow;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Component
@Slf4j
public class ReportExcelProcessor {
  private static final String TEMPLATE_FILE_PATH = "classpath:excel-templates/%s.xlsx";
  private static final String REPORT_DIR_PATH = "classpath:reports";
  private static final Pattern INDICATOR_PATTERN = Pattern.compile("\\((?<indicator>.*)\\)");
  private static final String SUM_PREFIX = "SUM_";
  private static final String SUM_SPECIFIC_PREFIX = SUM_PREFIX + "SPECIFIC_";
  private static final String IGNORE_POSTFIX = "__";

  public XSSFWorkbook cloneWorkbook(String alias) throws IOException, InvalidFormatException {
    var templatePath = ResourceUtils.getFile(String.format(TEMPLATE_FILE_PATH, alias)).toPath();
    var tmpPath = Paths.get(System.getProperty("java.io.tmpdir"), System.currentTimeMillis() + templatePath.getFileName().toString());
    Files.copy(templatePath, tmpPath);
    return new XSSFWorkbook(tmpPath.toFile());
  }

  public Map<String, Double> processSumsMap(
      XSSFWorkbook workbook, int shift, OffsetDateTime recordingDate, List<ReportRow> rows) {
    var sheet = workbook.getSheetAt(shift - 1);

    var localDateTime = ElectricTimeUtil.convertOffsetDateTimeToLocalTime(recordingDate);
    var dayOfWeek = localDateTime.getDayOfWeek();
    var indicators = getIndicatorsMap(sheet);

    rows.forEach(row -> fillBaseValue(sheet, indicators, dayOfWeek, shift, row));
    XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook); // force revaluate all formulas
    return getSumsMap(sheet, indicators);
  }

  public void resetDevelopmentCells(XSSFWorkbook workbook, int shift) {
    var sheet = workbook.getSheetAt(shift - 1);
    var indicators = getIndicatorsMap(sheet);
    indicators.forEach((indicator, address) -> {
      clearComment(sheet, address);
      if (indicator.startsWith(SUM_SPECIFIC_PREFIX)) {
        resetCell(sheet, address);
      }
    });
  }

  public void save(XSSFWorkbook workbook) {
    try {
      var path =
          Paths.get(
              String.valueOf(ResourceUtils.getFile(REPORT_DIR_PATH)),
              System.currentTimeMillis() + ".xlsx");
      var fos = new FileOutputStream(path.toString());
      workbook.write(fos);
      fos.close();
      workbook.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Map<String, CellAddress> getIndicatorsMap(XSSFSheet sheet) {
    var indicators = new HashMap<String, CellAddress>();
    var comments = sheet.getCellComments();
    comments.forEach(
        (address, comment) -> {
          var content = comment.getString().toString();
          var matcher = INDICATOR_PATTERN.matcher(content);
          if (!matcher.matches()) {
            return;
          }
          var indicator = matcher.group("indicator");
          indicators.put(indicator, address);
        });
    return indicators;
  }

  private void fillBaseValue(
      XSSFSheet sheet,
      Map<String, CellAddress> indicators,
      DayOfWeek dayOfWeek,
      int shift,
      ReportRow row) {
    var indicator = row.getIndicator();

    fill(
        sheet,
        indicators.get(
            indicator
                + "_0_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek,
                    shift == 1 ? SHIFT_1_PERIOD_1_START_TIME : SHIFT_2_PERIOD_1_START_TIME)),
        row.getOldElectricValue());
    fill(
        sheet,
        indicators.get(
            indicator
                + "_1_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek, shift == 1 ? SHIFT_1_PERIOD_1_END_TIME : SHIFT_2_PERIOD_1_END_TIME)),
        row.getNewElectricValue1());

    fill(
        sheet,
        indicators.get(
            indicator
                + "_2_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek,
                    shift == 1 ? SHIFT_1_PERIOD_2_START_TIME : SHIFT_2_PERIOD_2_START_TIME)),
        row.getNewElectricValue1());
    fill(
        sheet,
        indicators.get(
            indicator
                + "_3_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek, shift == 1 ? SHIFT_1_PERIOD_2_END_TIME : SHIFT_2_PERIOD_2_END_TIME)),
        row.getNewElectricValue2());

    fill(
        sheet,
        indicators.get(
            indicator
                + "_4_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek,
                    shift == 1 ? SHIFT_1_PERIOD_3_START_TIME : SHIFT_2_PERIOD_3_START_TIME)),
        row.getNewElectricValue2());
    fill(
        sheet,
        indicators.get(
            indicator
                + "_5_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek, shift == 1 ? SHIFT_1_PERIOD_3_END_TIME : SHIFT_2_PERIOD_3_END_TIME)),
        row.getNewElectricValue3());

    fill(
        sheet,
        indicators.get(
            indicator
                + "_6_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek,
                    shift == 1 ? SHIFT_1_PERIOD_4_START_TIME : SHIFT_2_PERIOD_4_START_TIME)),
        row.getNewElectricValue3());
    fill(
        sheet,
        indicators.get(
            indicator
                + "_7_"
                + ElectricTimeUtil.getTimeGroup(
                    dayOfWeek, shift == 1 ? SHIFT_1_PERIOD_4_END_TIME : SHIFT_2_PERIOD_4_END_TIME)),
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
    var cell = sheet.getRow(address.getRow()).getCell(address.getColumn());
    switch (cell.getCellType()) {
      case NUMERIC -> {
        return Optional.of(cell.getNumericCellValue());
      }
      case FORMULA -> {
        if (cell.getCachedFormulaResultType() == CellType.NUMERIC) {
          return Optional.of(cell.getNumericCellValue());
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
}
