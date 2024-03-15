package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand
import com.hbc.pms.core.api.controller.v1.request.UpdateAlarmConditionCommand
import com.hbc.pms.core.api.controller.v1.response.AlarmConditionResponse
import com.hbc.pms.core.api.support.error.ErrorCode
import com.hbc.pms.core.api.support.response.ApiResponse
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.api.utils.StringUtils
import com.hbc.pms.core.model.enums.AlarmActionType
import com.hbc.pms.core.model.enums.AlarmSeverity
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.repository.AlarmActionRepository
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.AlarmHistoryRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.support.spock.test.RestClient
import java.util.concurrent.ThreadLocalRandom
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.PendingFeature

class AlarmConditionControllerFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  private RestClient restClient

  @Autowired
  SensorConfigurationRepository sensorConfigurationRepository

  @Autowired
  AlarmConditionRepository alarmConditionRepository

  @Autowired
  AlarmActionRepository alarmActionRepository

  @Autowired
  AlarmHistoryRepository alarmHistoryRepository

  def createDefaultAlarmActionCommand() {
    return new CreateAlarmConditionCommand.AlarmActionCommand(type: AlarmActionType.EMAIL,
            message: "Email action's message",
            recipients: new HashSet<String>() {
              {
                add("thisisemail@gmail.com")
                add("haiz@metqua.com")
              }
            })
  }

  def createDefaultAlarmConditionCommand(sensorConfiguration) {
    return new CreateAlarmConditionCommand(sensorConfigurationId: sensorConfiguration.id,
            message: "High temperature detected",
            severity: AlarmSeverity.HIGH,
            type: AlarmType.CUSTOM,
            checkInterval: 30,
            timeDelay: 60,
            min: 20.0,
            max: 30.0,
            isEnabled: true,
            actions: [createDefaultAlarmActionCommand()])
  }

  def createDefaultUpdateConditionCommand() {
    return new UpdateAlarmConditionCommand(
            severity: AlarmSeverity.HIGH,
            type: AlarmType.CUSTOM,
            checkInterval: ThreadLocalRandom.current().nextInt(1, 3601),
            timeDelay: ThreadLocalRandom.current().nextInt(1, 3601),
            min: ThreadLocalRandom.current().nextDouble(1, 40),
            max: ThreadLocalRandom.current().nextDouble(40, 100),
            isEnabled: true)
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
            = sensorConfigurationRepository.findById(TestDataFixture.REAL_SENSOR_WITHOUT_CONDITION_ID).get()
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
    value << [-1, 0, 3601]
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
    value << [-1, 0, 3601]
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
    def conditionCountAfter = alarmConditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter

    where:
    value << [-1, 0, 3601]
  }

  @PendingFeature
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

  def "Create new alarm action - OK"() {
    given:
    def condition = alarmConditionRepository.findAll().first()
    def createActionCommand = createDefaultAlarmActionCommand()

    when:
    def response
            = restClient.post("/alarm-conditions/${condition.getId()}/actions",
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
            = restClient.post("/alarm-conditions/123/actions",
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
            = restClient.post("/alarm-conditions/${condition.id}/actions",
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
    given:
    def condition = alarmConditionRepository.findAll().first()
    def updateConditionCommand = createDefaultUpdateConditionCommand()

    when:
    def response
            = restClient.put("/alarm-conditions/${condition.id}",
            updateConditionCommand,
            ApiResponse<AlarmConditionResponse>)

    then:
    response.statusCode.is2xxSuccessful()
    def updatedCondition = alarmConditionRepository.findById(condition.id)
    verifyAll(updatedCondition.get()) {
      type == updateConditionCommand.getType()
      cron == StringUtils.buildCronFromSeconds(updateConditionCommand.checkInterval)
      timeDelay == updateConditionCommand.getTimeDelay()
      min == updateConditionCommand.min
      max == updateConditionCommand.max
      enabled == updateConditionCommand.getIsEnabled()
    }
  }

  def "Update alarm condition - Update alarm type - Bad request"() {
    given:
    def condition = alarmConditionRepository.findAll().first()
    def updateConditionCommand = createDefaultUpdateConditionCommand()
    updateConditionCommand.setType(AlarmType.PREDEFINED)

    when:
    def response
            = restClient.put("/alarm-conditions/${condition.id}",
            updateConditionCommand,
            ApiResponse<AlarmConditionResponse>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
  }

  @PendingFeature
  def "Update alarm condition - Update min value only - OK and max must be null"() {
    given:
    def condition = alarmConditionRepository.findAll().first()
    def updateConditionCommand = createDefaultUpdateConditionCommand()
    updateConditionCommand.setMax(null)

    when:
    def response
            = restClient.put("/alarm-conditions/${condition.id}",
            updateConditionCommand,
            ApiResponse<AlarmConditionResponse>)

    then:
    response.statusCode.is2xxSuccessful()
    response.body.data["max"] == null
  }

  @PendingFeature
  def "Update alarm condition - Update max value only - OK and min must be null"() {
    given:
    def condition = alarmConditionRepository.findAll().first()
    def updateConditionCommand = createDefaultUpdateConditionCommand()
    updateConditionCommand.setMin(null)

    when:
    def response
            = restClient.put("/alarm-conditions/${condition.id}",
            updateConditionCommand,
            ApiResponse<AlarmConditionResponse>)

    then:
    response.statusCode.is2xxSuccessful()
    response.body.data["min"] == null
  }

  def "Update alarm condition - Not existing alarm condition - Bad request"() {
    given:
    def updateConditionCommand = createDefaultUpdateConditionCommand()

    when:
    def response
            = restClient.put("/alarm-conditions/123",
            updateConditionCommand,
            ApiResponse<AlarmConditionResponse>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E404.toString()
    response.body.error["data"].containsIgnoreCase("Not found")
  }

  //TODO
  def "Update message of all actions in alarm condition - OK"() {}
  //TODO
  def "Update alarm action - OK"() {
  }
  //TODO
  def "Update alarm action - Not existing alarm action - Bad request"() {

  }
  //TODO
  def "Update alarm action - Not existing alarm condition - Bad request"() {

  }
  //TODO
  def "Update alarm action - Delete recipients of EMAIL action - Bad request"() {

  }

  def "Delete alarm condition - OK and cascade deleted alarm histories"() {
    given:
    def condition = alarmConditionRepository.findAll().first()
    def histories = condition.getHistories()

    when:
    restClient.delete("/alarm-conditions/${condition.id}")

    then:
    def deletedCondition = alarmConditionRepository.findById(condition.id)
    def deletedHistories
            = alarmHistoryRepository.findAllById(
            histories.collect { it -> it.id }
    )
    deletedCondition.isEmpty()
    deletedHistories.size() == 0
  }

  def "Delete alarm condition - Not existing alarm condition - Bad request"() {
    given:
    def conditionCountBefore = alarmConditionRepository.findAll().size()

    when:
    restClient.delete("/alarm-conditions/123")

    then:
    def conditionCountAfter = alarmConditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter
  }

  def "Delete alarm action - OK"() {
    given:
    def condition = alarmConditionRepository.findAll().first()
    def action = condition.getActions().get(0)

    when:
    restClient.delete("/alarm-conditions/${condition.id}/actions/${action.id}")

    then:
    def updatedCondition = alarmConditionRepository.findById(condition.id)
    def deletedAction = alarmActionRepository.findById(action.id)
    updatedCondition.get().actions.size() == condition.actions.size() - 1
    deletedAction.isEmpty()
  }

  def "Delete alarm action - Not existing alarm condition - Bad request"() {
    given:
    def condition = alarmConditionRepository.findAll().first()
    def actionCountBefore = condition.getActions().size()

    when:
    restClient.delete("/alarm-conditions/${condition.id}/actions/123")

    then:
    def actionCountAfter
            = alarmConditionRepository.findById(condition.id).get().getActions().size()
    actionCountBefore == actionCountAfter
  }

  @PendingFeature
  def "Delete alarm action - Not existing alarm action - Bad request"() {
    given:
    def condition = alarmConditionRepository.findAll().first()
    def action = condition.getActions().get(0)
    def actionCountBefore = condition.getActions().size()

    when:
    restClient.delete("/alarm-conditions/123/actions/${action.id}")

    then:
    def actionCountAfter
            = alarmConditionRepository.findById(condition.id).get().getActions().size()
    actionCountBefore == actionCountAfter
  }
}
