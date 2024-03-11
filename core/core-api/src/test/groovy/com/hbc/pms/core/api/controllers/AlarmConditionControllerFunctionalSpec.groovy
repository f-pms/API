package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand
import com.hbc.pms.core.api.controller.v1.response.AlarmConditionResponse
import com.hbc.pms.core.api.support.response.ApiResponse
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.enums.AlarmActionType
import com.hbc.pms.core.model.enums.AlarmSeverity
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.support.spock.test.RestClient
import org.springframework.beans.factory.annotation.Autowired

class AlarmConditionControllerFunctionalSpec extends FunctionalTestSpec {

  @Autowired
  private RestClient restClient
  @Autowired
  SensorConfigurationRepository sensorConfigurationRepository
  @Autowired
  AlarmConditionRepository alarmConditionRepository

  def "Get all alarms - OK"() {
    when:
    def response = restClient.get("/alarm-conditions", ApiResponse<List<AlarmConditionResponse>>)

    then:
    response.statusCode.is2xxSuccessful()
    List<AlarmConditionResponse> listCondition = response.body.data as List<AlarmConditionResponse>
    listCondition.size() == 2
  }

  def "Create a new alarm - OK"() {
    given:
    def sensorConfiguration = sensorConfigurationRepository.findAllByAddress(TestDataFixture.PLC_ADDRESS_REAL_02).first()
    def createActionCommand = new CreateAlarmConditionCommand(
            sensorConfigurationId: sensorConfiguration.id,
            message: "High temperature detected",
            severity: AlarmSeverity.HIGH,
            type: AlarmType.CUSTOM,
            checkInterval: 300,
            timeDelay: 60,
            min: 20.0,
            max: 30.0,
            isEnabled: true,
            actions: [new CreateAlarmConditionCommand.AlarmActionCommand(
                    type: AlarmActionType.POPUP,
                    message: "Threshold exceeded"
            )]
    )
    when:
    def response = restClient.post("/alarm-conditions", createActionCommand, ApiResponse.class)

    then:
    response.statusCode.is2xxSuccessful()
    int createdConditionId = response.body.data["id"] as int
    def createdCondition = alarmConditionRepository.findById(createdConditionId)
    assert createdCondition.isPresent()
    assert createdCondition.get().getSensorConfiguration().id == sensorConfiguration.id
  }

}
