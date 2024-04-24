package com.hbc.pms.core.api.service.alarm;

import com.hbc.pms.core.api.controller.v1.response.AlarmHistoryResponse;
import com.hbc.pms.core.api.service.blueprint.SensorConfigurationPersistenceService;
import com.hbc.pms.core.model.enums.AlarmStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmHistoryService {
    private final ModelMapper mapper;
    private final AlarmPersistenceService alarmPersistenceService;
    private final SensorConfigurationPersistenceService sensorConfigurationPersistenceService;

    public List<AlarmHistoryResponse> getAllHistoriesByStatus(AlarmStatus status) {
        var histories = alarmPersistenceService.getAllHistoriesByStatus(status)
                .stream().map(history -> mapper.map(history, AlarmHistoryResponse.class))
                .toList();
        histories.forEach(
                history ->
                        history.setBlueprint(
                                mapper.map(
                                        sensorConfigurationPersistenceService.getAssociatedBlueprint(
                                                history.getCondition().getSensorConfiguration().getId()),
                                        AlarmHistoryResponse.BlueprintForHistoryResponse.class)));
        return histories;
    }
}
