package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.controller.v1.response.AlarmHistoryResponse;
import com.hbc.pms.core.api.service.alarm.AlarmHistoryService;
import com.hbc.pms.core.api.service.alarm.AlarmPersistenceService;
import com.hbc.pms.core.model.enums.AlarmStatus;
import com.hbc.pms.support.web.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${apiPrefix}/alarm-histories")
@RequiredArgsConstructor
public class AlarmHistoryController {
  private final ModelMapper mapper;
  private final AlarmHistoryService alarmHistoryService;

  @GetMapping
  public ApiResponse<List<AlarmHistoryResponse>> getAll(AlarmStatus status) {
    List<AlarmHistoryResponse> alarmHistories = alarmHistoryService.getAllHistoriesByStatus(status);
    return ApiResponse.success(alarmHistories);
  }
}
