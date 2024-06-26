package com.hbc.pms.core.api.service.report;

import com.hbc.pms.core.api.config.report.ReportConfiguration;
import com.hbc.pms.core.api.constant.ErrorMessageConstant;
import com.hbc.pms.core.api.support.data.ReportExcelProcessor;
import com.hbc.pms.core.model.criteria.ReportCriteria;
import com.hbc.pms.support.web.error.CoreApiException;
import com.hbc.pms.support.web.error.ErrorType;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportDownloaderService {
  private final ReportPersistenceService reportPersistenceService;
  private final ReportExcelProcessor processor;
  private final ReportGenerationService reportGenerationService;
  private final ReportConfiguration reportConfiguration;

  public void download(List<Path> paths, HttpServletResponse response) {
    response.setContentType("application/zip");
    response.setHeader(
        HttpHeaders.CONTENT_DISPOSITION,
        ContentDisposition.attachment()
            .filename("reports.zip", StandardCharsets.UTF_8)
            .build()
            .toString());
    try {
      var zipOutputStream = new ZipOutputStream(response.getOutputStream());
      for (Path path : paths) {
        var inputStream = Files.newInputStream(path);
        var file = path.toFile();
        var entry = new ZipEntry(file.getParentFile().getName() + "/" + file.getName());
        zipOutputStream.putNextEntry(entry);
        StreamUtils.copy(inputStream, zipOutputStream);
        zipOutputStream.flush();
        zipOutputStream.closeEntry();
      }
      zipOutputStream.finish();
      zipOutputStream.close();
    } catch (IOException e) {
      throw new CoreApiException(
          ErrorType.DEFAULT_ERROR, ErrorMessageConstant.DOWNLOAD_EXCEL_FILES_FAILDED);
    }
  }

  public List<Path> getReportPaths(ReportCriteria criteria) {
    var reports = reportPersistenceService.getAll(criteria);
    reportGenerationService.generateMissingExcelFiles(reports);
    return reports.stream()
        .map(
            report ->
                Paths.get(
                    reportConfiguration.getDir(),
                    report.getType().getName(),
                    processor.getFileName(report)))
        .filter(path -> path.toFile().exists())
        .toList();
  }
}
