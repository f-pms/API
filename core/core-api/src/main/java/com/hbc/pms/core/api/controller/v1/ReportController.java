package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.controller.v1.response.ReportResponse;
import com.hbc.pms.core.api.service.report.ReportDownloaderService;
import com.hbc.pms.core.api.service.report.ReportPersistenceService;
import com.hbc.pms.core.api.service.report.ReportTypePersistenceService;
import com.hbc.pms.core.model.Report;
import com.hbc.pms.core.model.ReportType;
import com.hbc.pms.core.model.criteria.ReportCriteria;
import com.hbc.pms.support.web.pagination.QueryResult;
import com.hbc.pms.support.web.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reports")
@RequiredArgsConstructor
public class ReportController {
  private final ModelMapper mapper;
  private final ReportTypePersistenceService reportTypePersistenceService;
  private final ReportPersistenceService reportPersistenceService;
  private final ReportDownloaderService reportDownloaderService;

  @GetMapping("types")
  public ApiResponse<List<ReportType>> getTypes() {
    return ApiResponse.success(reportTypePersistenceService.getAll());
  }

  @GetMapping("details")
  public ApiResponse<QueryResult<ReportResponse>> getDetails(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      ReportCriteria filter) {
    Page<Report> reports = reportPersistenceService.getAll(filter, PageRequest.of(page - 1, size));

    return ApiResponse.success(
        QueryResult.fromPage(reports.map(report -> mapper.map(report, ReportResponse.class))));
  }

  @GetMapping("details/{id}")
  public ApiResponse<ReportResponse> getDetailById(@PathVariable Long id) {
    return ApiResponse.success(
        mapper.map(reportPersistenceService.getByIdWithRows(id), ReportResponse.class));
  }

  @GetMapping("/download")
  public void download(
      ReportCriteria criteria,
      HttpServletResponse response) {
    var paths = reportDownloaderService.getReportPaths(criteria);
    reportDownloaderService.download(paths, response);
  }
}
