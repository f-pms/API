package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.support.error.ErrorCode
import com.hbc.pms.core.api.support.response.ApiResponse
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.repository.AlarmActionRepository
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.support.spock.test.RestClient
import org.springframework.beans.factory.annotation.Autowired

class AlarmConditionControllerCreateEndpointsFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  AlarmConditionRepository conditionRepository

  @Autowired
  AlarmActionRepository actionRepository

  @Autowired
  RestClient restClient

  Long REAL_SENSOR_WITHOUT_CONDITION_ID
  Long REAL_SENSOR_WITH_CONDITION_ID

  def setup() {
    def monitorBlueprint
            = blueprintRepository.findById(TestDataFixture.MONITORING_BLUEPRINT_ID).get()
    def sensorConfig
            = configurationRepository
            .save(
                    TestDataFixture.createSensorConfiguration(monitorBlueprint, TestDataFixture.PLC_ADDRESS_REAL_01)
            )
    REAL_SENSOR_WITHOUT_CONDITION_ID = sensorConfig.getId()

    def sensorConfig2
            = configurationRepository
            .save(
                    TestDataFixture.createSensorConfiguration(monitorBlueprint, TestDataFixture.PLC_ADDRESS_BOOL_02)
            )
    REAL_SENSOR_WITH_CONDITION_ID = sensorConfig2.getId()
    conditionRepository.save(TestDataFixture.createDefaultConditionEntity(AlarmType.PREDEFINED, sensorConfig2))
  }

  def "Create new alarm condition - OK"() {
    given:
    def sensorConfiguration
            = configurationRepository.findById(REAL_SENSOR_WITHOUT_CONDITION_ID).get()
    def createConditionCommand
            = TestDataFixture.createDefaultAlarmConditionCommand(sensorConfiguration)

    when:
    def response
            = restClient.post("/alarm-conditions", createConditionCommand, ApiResponse.class)

    then:
    response.statusCode.is2xxSuccessful()
    int createdConditionId = response.body.data["id"] as int
    def createdCondition = conditionRepository.findById(createdConditionId)
    assert createdCondition.isPresent()
    assert createdCondition.get().getSensorConfiguration().id == sensorConfiguration.id
  }

  def "Create new alarm condition - Invalid value (#value) for checkInterval - Bad request"() {
    given:
    def sensorConfiguration
            = configurationRepository.findById(REAL_SENSOR_WITHOUT_CONDITION_ID).get()
    def createConditionCommand
            = TestDataFixture.createDefaultAlarmConditionCommand(sensorConfiguration)
    createConditionCommand.setCheckInterval(value)
    def conditionCountBefore = conditionRepository.findAll().size()

    when:
    def response
            = restClient.post("/alarm-conditions", createConditionCommand, ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
    def conditionCountAfter = conditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter

    where:
    value << [-1, 0, 3601]
  }

  def "Create new alarm condition - Invalid value (#value) for timeDelay - Bad request"() {
    given:
    def sensorConfiguration
            = configurationRepository.findById(REAL_SENSOR_WITHOUT_CONDITION_ID).get()
    def createConditionCommand
            = TestDataFixture.createDefaultAlarmConditionCommand(sensorConfiguration)
    createConditionCommand.setTimeDelay(value)
    def conditionCountBefore = conditionRepository.findAll().size()

    when:
    def response
            = restClient.post("/alarm-conditions", createConditionCommand, ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
    response.body.error.data["timeDelay"]
    def conditionCountAfter = conditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter

    where:
    value << [-1, 0, 3601]
  }

  def "Create new alarm condition - CUSTOM condition with both min & max = null - Bad request"() {
    given:
    def sensorConfiguration
            = configurationRepository.findAllByAddress(TestDataFixture.PLC_ADDRESS_REAL_01).first()
    def createConditionCommand
            = TestDataFixture.createDefaultAlarmConditionCommand(sensorConfiguration)
    createConditionCommand.setMin(null)
    createConditionCommand.setMax(null)
    def conditionCountBefore = conditionRepository.findAll().size()

    when:
    def response
            = restClient.post("/alarm-conditions", createConditionCommand, ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
    def conditionCountAfter = conditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter

    where:
    value << [-1, 0, 3601]
  }

  def "Create new alarm condition - CUSTOM condition with min greater than or equal max - Bad request"() {
    given:
    def sensorConfiguration
            = configurationRepository.findById(REAL_SENSOR_WITH_CONDITION_ID).get()
    def createConditionCommand
            = TestDataFixture.createDefaultAlarmConditionCommand(sensorConfiguration)
    createConditionCommand.setMin(120)
    createConditionCommand.setMax(12)
    def conditionCountBefore = conditionRepository.findAll().size()

    when:
    def response
            = restClient.post("/alarm-conditions", createConditionCommand, ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
    def conditionCountAfter = conditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter
  }

  def "Create new alarm condition - Existed condition with Tag configuration - Bad request"() {
    given:
    def sensorConfiguration
            = configurationRepository.findById(REAL_SENSOR_WITH_CONDITION_ID).get()
    def createConditionCommand
            = TestDataFixture.createDefaultAlarmConditionCommand(sensorConfiguration)
    def conditionCountBefore = conditionRepository.findAll().size()

    when:
    def response
            = restClient.post("/alarm-conditions", createConditionCommand, ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
    response.body.error["data"].containsIgnoreCase("Existed")
    def conditionCountAfter = conditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter
  }

  def "Create new alarm action - OK"() {
    given:
    def condition = conditionRepository.findAll().first()
    def createActionCommand = TestDataFixture.createDefaultAlarmActionCommand()

    when:
    def response
            = restClient.post("/alarm-conditions/${condition.getId()}/actions",
            createActionCommand,
            ApiResponse.class)

    then:
    response.statusCode.is2xxSuccessful()
    int createdActionId = response.body.data["id"] as int
    def createdAction = actionRepository.findById(createdActionId)
    createdAction.isPresent()
    createdAction.get().getCondition().getId() == condition.getId()
  }

  def "Create new alarm action - Not existing alarm condition - Not found and Bad request"() {
    given:
    def createActionCommand = TestDataFixture.createDefaultAlarmActionCommand()
    def actionCountBefore = actionRepository.findAll().size()

    when:
    def response
            = restClient.post("/alarm-conditions/123/actions",
            createActionCommand,
            ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E404.toString()
    response.body.error["data"].containsIgnoreCase("Not found")
    def actionCountAfter = actionRepository.findAll().size()
    actionCountBefore == actionCountAfter
  }

  def "Create new alarm action - EMAIL action with no recipient - Bad request"() {
    given:
    def condition = conditionRepository.findAll().first()
    def createActionCommand = TestDataFixture.createDefaultAlarmActionCommand()
    createActionCommand.setRecipients(null)
    def actionCountBefore = actionRepository.findAll().size()

    when:
    def response
            = restClient.post("/alarm-conditions/${condition.id}/actions",
            createActionCommand,
            ApiResponse.class)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
    response.body.error.data["validRecipients"]
    def actionCountAfter = actionRepository.findAll().size()
    actionCountBefore == actionCountAfter
  }
}
