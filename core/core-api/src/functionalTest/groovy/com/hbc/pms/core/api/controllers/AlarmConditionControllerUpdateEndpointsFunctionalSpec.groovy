package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.response.AlarmConditionResponse
import com.hbc.pms.core.api.support.error.ErrorCode
import com.hbc.pms.core.api.support.response.ApiResponse
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.api.utils.StringUtils
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.support.spock.test.RestClient
import org.springframework.beans.factory.annotation.Autowired

class AlarmConditionControllerUpdateEndpointsFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  AlarmConditionRepository conditionRepository

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  RestClient restClient

  def setup() {
    def monitorBlueprint
            = blueprintRepository.findById(TestDataFixture.CUSTOM_ALARM_BLUEPRINT_ID).get()
    def sensorConfig
            = configurationRepository
            .save(
                    TestDataFixture.createSensorConfiguration(monitorBlueprint, TestDataFixture.PLC_ADDRESS_REAL_01)
            )
    conditionRepository.save(TestDataFixture.createDefaultConditionEntity(AlarmType.CUSTOM, sensorConfig))
  }

  def "Update alarm condition - OK"() {
    given:
    def condition = conditionRepository.findAll().first()
    def updateConditionCommand = TestDataFixture.createDefaultUpdateConditionCommand()

    when:
    def response
            = restClient.put("${ALARM_CONDITION_PATH}/${condition.id}",
            updateConditionCommand,
            ApiResponse<AlarmConditionResponse>)

    then:
    response.statusCode.is2xxSuccessful()
    def updatedCondition = conditionRepository.findById(condition.id)
    verifyAll(updatedCondition.get()) {
      getType() == updateConditionCommand.type
      getCron() == StringUtils.buildCronFromSeconds(updateConditionCommand.checkInterval)
      getTimeDelay() == updateConditionCommand.timeDelay
      getMin() == updateConditionCommand.min
      getMax() == updateConditionCommand.max
    }
  }

  def "Update alarm condition - Update alarm type - Bad request"() {
    given:
    def condition = conditionRepository.findAll().first()
    def updateConditionCommand = TestDataFixture.createDefaultUpdateConditionCommand()
    updateConditionCommand.setType(AlarmType.PREDEFINED)

    when:
    def response
            = restClient.put("${ALARM_CONDITION_PATH}/${condition.id}",
            updateConditionCommand,
            ApiResponse<AlarmConditionResponse>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
  }

  def "Update alarm condition - Update min value only - OK and max must be null"() {
    given:
    def condition = conditionRepository.findAll().first()
    def updateConditionCommand = TestDataFixture.createDefaultUpdateConditionCommand()
    updateConditionCommand.setMax(null)

    when:
    def response
            = restClient.put("${ALARM_CONDITION_PATH}/${condition.id}",
            updateConditionCommand,
            ApiResponse<AlarmConditionResponse>)

    then:
    response.statusCode.is2xxSuccessful()
    response.body.data["max"] == null
  }

  def "Update alarm condition - Update max value only - OK and min must be null"() {
    given:
    def condition = conditionRepository.findAll().first()
    def updateConditionCommand = TestDataFixture.createDefaultUpdateConditionCommand()
    updateConditionCommand.setMin(null)

    when:
    def response
            = restClient.put("${ALARM_CONDITION_PATH}/${condition.id}",
            updateConditionCommand,
            ApiResponse<AlarmConditionResponse>)

    then:
    response.statusCode.is2xxSuccessful()
    response.body.data["min"] == null
  }

  def "Update alarm condition - Not existing alarm condition - Bad request"() {
    given:
    def updateConditionCommand = TestDataFixture.createDefaultUpdateConditionCommand()

    when:
    def response
            = restClient.put("${ALARM_CONDITION_PATH}/123",
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
}
