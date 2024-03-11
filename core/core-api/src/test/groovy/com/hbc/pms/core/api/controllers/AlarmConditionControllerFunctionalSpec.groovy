package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand
import com.hbc.pms.core.api.controller.v1.response.AlarmConditionResponse
import com.hbc.pms.core.api.support.error.ErrorCode
import com.hbc.pms.core.api.support.response.ApiResponse
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.enums.AlarmActionType
import com.hbc.pms.core.model.enums.AlarmSeverity
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.repository.AlarmActionRepository
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

  @Autowired
  AlarmActionRepository alarmActionRepository

  def createDefaultAlarmActionCommand() {
    def createActionCommand = new CreateAlarmConditionCommand.AlarmActionCommand(
            type: AlarmActionType.EMAIL,
            message: "Email action's message",
            recipients: new HashSet<String>() {
              {
                add("thisisemail@gmail.com")
                add("haiz@metqua.com")
              }
            }
    )
    return createActionCommand
  }

  def createDefaultAlarmConditionCommand(sensorConfiguration) {
    def createConditionCommand = new CreateAlarmConditionCommand(
            sensorConfigurationId: sensorConfiguration.id,
            message: "High temperature detected",
            severity: AlarmSeverity.HIGH,
            type: AlarmType.CUSTOM,
            checkInterval: 30,
            timeDelay: 60,
            min: 20.0,
            max: 30.0,
            isEnabled: true,
            actions: [
                    createDefaultAlarmActionCommand()
            ])
    return createConditionCommand
  }

  def "Get all alarm conditions - OK"() {
    when:
    def response = restClient.get("/alarm-conditions", ApiResponse<List<AlarmConditionResponse>>)

    then:
    response.statusCode.is2xxSuccessful()
    List<AlarmConditionResponse> listCondition = response.body.data as List<AlarmConditionResponse>
    listCondition.size() == 1
  }

  def "Get alarm condition by Id - OK"() {
    when:
    def condition = alarmConditionRepository.findAll().first()
    def response
            = restClient.get("/alarm-conditions/${condition.id}", ApiResponse<AlarmConditionResponse>)

    then:
    response.statusCode.is2xxSuccessful()
  }

  def "Get alarm condition by Id - Not found and Bad request"() {
    when:
    def response
            = restClient.get("/alarm-conditions/12", ApiResponse<RuntimeException>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E404.toString()
  }

  def "Get alarm condition by Id - Invalid format input - Bad request"() {
    when:
    def response
            = restClient.get("/alarm-conditions/abc",
            ApiResponse<List<AlarmConditionResponse>>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
  }

  def "Create new alarm condition - OK"() {
    given:
    def sensorConfiguration
            = sensorConfigurationRepository.findAllByAddress(TestDataFixture.PLC_ADDRESS_REAL_02).first()
    def createConditionCommand = createDefaultAlarmConditionCommand(sensorConfiguration)

    when:
    def response = restClient.post("/alarm-conditions", createConditionCommand, ApiResponse.class)

    then:
    response.statusCode.is2xxSuccessful()
    int createdConditionId = response.body.data["id"] as int
    def createdCondition = alarmConditionRepository.findById(createdConditionId)
    assert createdCondition.isPresent()
    assert createdCondition.get().getSensorConfiguration().id == sensorConfiguration.id
  }

  def "Create new alarm condition - Invalid value (#value) for checkInterval - Bad request"() {
    given:
    def sensorConfiguration = sensorConfigurationRepository.findAllByAddress(TestDataFixture.PLC_ADDRESS_REAL_02).first()
    def createConditionCommand = createDefaultAlarmConditionCommand(sensorConfiguration)
    createConditionCommand.setCheckInterval(value)
    def conditionCountBefore = alarmConditionRepository.findAll().size()

    when:
    def response = restClient.post("/alarm-conditions", createConditionCommand, ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
    def conditionCountAfter = alarmConditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter

    where:
    value | _
    -1    | _
    0     | _
    3601  | _
  }

  def "Create new alarm condition - Invalid value (#value) for timeDelay - Bad request"() {
    given:
    def sensorConfiguration = sensorConfigurationRepository.findAllByAddress(TestDataFixture.PLC_ADDRESS_REAL_02).first()
    def createConditionCommand = createDefaultAlarmConditionCommand(sensorConfiguration)
    createConditionCommand.setTimeDelay(value)
    def conditionCountBefore = alarmConditionRepository.findAll().size()

    when:
    def response = restClient.post("/alarm-conditions", createConditionCommand, ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
    response.body.error.data["timeDelay"]
    def conditionCountAfter = alarmConditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter

    where:
    value | _
    -1    | _
    0     | _
    3601  | _
  }

  def "Create new alarm condition - CUSTOM condition with both min & max = null - Bad request"() {
    given:
    def sensorConfiguration
            = sensorConfigurationRepository.findAllByAddress(TestDataFixture.PLC_ADDRESS_REAL_02).first()
    def createConditionCommand = createDefaultAlarmConditionCommand(sensorConfiguration)
    createConditionCommand.setMin(null)
    createConditionCommand.setMax(null)
    def conditionCountBefore = alarmConditionRepository.findAll().size()

    when:
    def response = restClient.post("/alarm-conditions", createConditionCommand, ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
    response.body.error.data["min"]
    response.body.error.data["max"]
    def conditionCountAfter = alarmConditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter

    where:
    value | _
    -1    | _
    0     | _
    3601  | _
  }

 /* //Add more implementation to pass this test
  def "Create new alarm condition - CUSTOM condition with min greater than or equal max - Bad request"() {
    given:
    def sensorConfiguration
            = sensorConfigurationRepository.findAllByAddress(TestDataFixture.PLC_ADDRESS_REAL_02).first()
    def createConditionCommand = createDefaultAlarmConditionCommand(sensorConfiguration)
    createConditionCommand.setMin(120)
    createConditionCommand.setMax(12)
    def conditionCountBefore = alarmConditionRepository.findAll().size()

    when:
    def response = restClient.post("/alarm-conditions", createConditionCommand, ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
    def conditionCountAfter = alarmConditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter
  }

  def "Create new alarm condition - Existed condition with Tag configuration - Bad request"() {
    given:
    def sensorConfiguration
            = sensorConfigurationRepository.findAllByAddress(TestDataFixture.PLC_ADDRESS_REAL_01).first()
    def createConditionCommand = createDefaultAlarmConditionCommand(sensorConfiguration)
    def conditionCountBefore = alarmConditionRepository.findAll().size()

    when:
    def response = restClient.post("/alarm-conditions", createConditionCommand, ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
    response.body.error["data"].containsIgnoreCase("Existed")
    def conditionCountAfter = alarmConditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter
  }
*/
  def "Create new alarm action - OK"() {
    given:
    def condition = alarmConditionRepository.findAll().first()
    def createActionCommand = createDefaultAlarmActionCommand()

    when:
    def response
            = restClient.post(
            "/alarm-conditions/${condition.getId()}/actions",
            createActionCommand,
            ApiResponse.class)

    then:
    response.statusCode.is2xxSuccessful()
    int createdActionId = response.body.data["id"] as int
    def createdAction = alarmActionRepository.findById(createdActionId)
    createdAction.isPresent()
    createdAction.get().getCondition().getId() == condition.getId()
  }

  def "Create new alarm action - Not existing alarm condition - Not found and Bad request"() {
    given:
    def createActionCommand = createDefaultAlarmActionCommand()
    def actionCountBefore = alarmActionRepository.findAll().size()

    when:
    def response
            = restClient.post(
            "/alarm-conditions/123/actions",
            createActionCommand,
            ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E404.toString()
    response.body.error["data"].containsIgnoreCase("Not found")
    def actionCountAfter = alarmActionRepository.findAll().size()
    actionCountBefore == actionCountAfter
  }

  def "Create new alarm action - EMAIL action with no recipient - Bad request"() {
    given:
    def condition = alarmConditionRepository.findAll().first()
    def createActionCommand = createDefaultAlarmActionCommand()
    createActionCommand.setRecipients(null)
    def actionCountBefore = alarmActionRepository.findAll().size()

    when:
    def response
            = restClient.post(
            "/alarm-conditions/${condition.id}/actions",
            createActionCommand,
            ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
    response.body.error.data["validRecipients"]
    def actionCountAfter = alarmActionRepository.findAll().size()
    actionCountBefore == actionCountAfter
  }

  def "Update alarm condition - OK"() {

  }

  def "Update alarm condition - Update type - Bad request"() {

  }

  def "Update alarm condition - Update min value only - OK and max must be null"() {

  }

  def "Update alarm condition - Update max value only - OK and min must be null"() {

  }

  def "Update alarm condition - Not existing alarm condition - Bad request"() {

  }

  def "Update alarm action - OK"() {

  }

  def "Update alarm action - Not existing alarm action - Bad request"() {

  }

  def "Update alarm action - Not existing alarm condition - Bad request"() {

  }

  def "Update alarm action - Delete recipients of EMAIL action - Bad request"() {

  }

  def "Delete alarm condition - OK and cascade deleted alarm histories"() {

  }

  def "Delete alarm condition - Not existing alarm condition - Bad request"() {

  }

  def "Delete alarm action - OK"() {

  }

  def "Delete alarm action - Not existing alarm condition - Bad request"() {

  }

  def "Delete alarm action - Not existing alarm action - Bad request"() {

  }

}
