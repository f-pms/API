package com.hbc.pms.core.api.support.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Component
@Slf4j
public class ReportProcessor {
  private final static String TEMPLATE_PATH = "classpath:excel-templates/%s.xlsx";
  private final static Pattern INDICATOR_PATTERN = Pattern.compile("\\((?<indicator>.*)\\)");

  public void process(Map<String, Double> rowsMap) throws IOException, InvalidFormatException {
    var templateFile = ResourceUtils.getFile(String.format(TEMPLATE_PATH, "DAM"));
    var workbook = new XSSFWorkbook(templateFile);
    var sheet = workbook.getSheetAt(0);
    var comments = sheet.getCellComments();
    comments.forEach(
        (c, v) -> {
          var content = v.getString().toString();
          var matcher = INDICATOR_PATTERN.matcher(content);
          if (!matcher.matches()) {
            return;
          }
          var indicator = matcher.group("indicator");
          if (indicator.startsWith("DAM_")) {
            fill(sheet, c, rowsMap.get(indicator + "_PEAK"));
          }
        });
    save(workbook);
  }

  private void fill(XSSFSheet sheet, CellAddress address, Double value) {
    var cell = sheet.getRow(address.getRow()).getCell(address.getColumn());
    cell.setCellValue(value);
  }

  private void save(Workbook workbook) throws FileNotFoundException {
    var path = ResourceUtils.getFile("classpath:reports/").toString() + System.currentTimeMillis() + ".xlsx";
    try {
      var fos = new FileOutputStream(path);
      workbook.write(fos);
      fos.flush();
      fos.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
