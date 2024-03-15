package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.response.AlarmConditionResponse
import com.hbc.pms.core.api.support.error.ErrorCode
import com.hbc.pms.core.api.support.response.ApiResponse
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.support.spock.test.RestClient
import org.springframework.beans.factory.annotation.Autowired


class AlarmConditionControllerQueryActionsFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  TestDataFixture dataFixture

  @Autowired
  RestClient restClient

  @Autowired
  AlarmConditionRepository conditionRepository

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  SensorConfigurationRepository configurationRepository

  Long DEFAULT_CONDITION_ID

  def setup() {
    populateDefaultData()
  }

  def populateDefaultData() {
    def monitorBlueprint
            = blueprintRepository.findById(TestDataFixture.MONITORING_BLUEPRINT_ID).get()
    def sensorConfig
            = configurationRepository
            .save(
                    TestDataFixture.createSensorConfiguration(monitorBlueprint, TestDataFixture.PLC_ADDRESS_REAL_01))
    def condition = conditionRepository.save(
            TestDataFixture.createDefaultConditionEntity(AlarmType.PREDEFINED, sensorConfig))

    DEFAULT_CONDITION_ID = condition.id
  }

  def "Get all alarm conditions - OK"() {
    when:
    def response
            = restClient.get("/alarm-conditions", ApiResponse<List<AlarmConditionResponse>>)

    then:
    response.statusCode.is2xxSuccessful()
    List<AlarmConditionResponse> listConditions = response.body.data as List<AlarmConditionResponse>
    listConditions.size() == 1
    listConditions.any { it["id"] == DEFAULT_CONDITION_ID }
  }

  def "Get alarm condition by Id - OK"() {
    when:
    def response
            = restClient.get("/alarm-conditions/$DEFAULT_CONDITION_ID", ApiResponse<AlarmConditionResponse>)

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
}
