package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.controller.v1.common.Page;
import com.hbc.pms.core.api.controller.v1.request.SearchMultiDayChartCommand;
import com.hbc.pms.core.api.controller.v1.response.ReportResponse;
import com.hbc.pms.core.api.service.ReportPersistenceService;
import com.hbc.pms.core.api.service.ReportService;
import com.hbc.pms.core.api.service.ReportTypePersistenceService;
import com.hbc.pms.core.model.ReportType;
import com.hbc.pms.core.model.criteria.ReportCriteria;
import com.hbc.pms.support.web.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
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
  private final ReportService reportService;

  @GetMapping("types")
  public ApiResponse<List<ReportType>> getTypes() {
    return ApiResponse.success(reportTypePersistenceService.getAll());
  }

  @GetMapping("details")
  public ApiResponse<Page<ReportResponse>> getDetails(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      ReportCriteria filter) {
    var reports = reportPersistenceService.getAll(filter, PageRequest.of(page - 1, size));
    return ApiResponse.success(
        Page.<ReportResponse>builder()
            .pageTotal(reports.getPageTotal())
            .recordTotal(reports.getRecordTotal())
            .content(
                reports.getContent().stream()
                    .map(el -> mapper.map(el, ReportResponse.class))
                    .toList())
            .build());
  }

  @GetMapping("details/{id}")
  public ApiResponse<ReportResponse> getDetailById(@PathVariable Long id) {
    return ApiResponse.success(
        mapper.map(reportPersistenceService.getByIdWithRows(id), ReportResponse.class));
  }

  @GetMapping("/{id}/charts/one-day")
  public ApiResponse<List<Map<String, Double>>> getOneDayChartFigures(@PathVariable Long id) {
    return ApiResponse.success(reportService.getOneDayChartFigures(id));
  }

  @GetMapping("/charts/multi-day/summary")
  public ApiResponse<Map<String, Double>> getMultiDaySummaryChartFigures(
      @Valid SearchMultiDayChartCommand searchCommand) {
    return ApiResponse.success(reportService.getMultiDaySummaryChartFigures(searchCommand));
  }
}
