package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.support.error.ErrorCode
import com.hbc.pms.core.api.support.response.ApiResponse
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import spock.lang.PendingFeature

class AlarmConditionControllerCreateEndpointsFunctionalSpec extends FunctionalTestSpec {
  def setup() {

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
}
