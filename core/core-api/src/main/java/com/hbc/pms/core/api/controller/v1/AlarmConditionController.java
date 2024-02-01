package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand;
import com.hbc.pms.core.api.service.AlarmConditionPersistenceService;
import com.hbc.pms.core.api.service.AlarmConditionService;
import com.hbc.pms.core.api.support.response.ApiResponse;
import com.hbc.pms.core.model.AlarmCondition;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("alarm-conditions")
@RequiredArgsConstructor
public class AlarmConditionController {
  private final AlarmConditionService alarmConditionService;

  @PostMapping()
  public ApiResponse<AlarmCondition> create(@Valid @RequestBody CreateAlarmConditionCommand body) {
    AlarmCondition result = alarmConditionService.createAlarmCondition(body);
    return ApiResponse.success(result);
  }
}
