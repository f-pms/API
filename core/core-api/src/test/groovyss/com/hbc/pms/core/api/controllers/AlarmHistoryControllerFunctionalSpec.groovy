package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.response.AlarmConditionResponse
import com.hbc.pms.core.api.controller.v1.response.AlarmHistoryResponse
import com.hbc.pms.core.api.support.response.ApiResponse
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.enums.AlarmStatus
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.AlarmHistoryRepository
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.support.spock.test.RestClient
import org.springframework.beans.factory.annotation.Autowired

class AlarmHistoryControllerFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  AlarmConditionRepository conditionRepository

  @Autowired
  AlarmHistoryRepository historyRepository

  @Autowired
  RestClient restClient

  Long CONDITION_ID

  def setup() {
    def blueprint
            = blueprintRepository.findById(TestDataFixture.PREDEFINED_ALARM_BLUEPRINT_ID).get()
    def sensorConfig
            = configurationRepository
            .save(
                    TestDataFixture.createSensorConfiguration(blueprint, TestDataFixture.PLC_ADDRESS_BOOL_01)
            )
    def condition
            = conditionRepository
            .save(TestDataFixture.createDefaultConditionEntity(AlarmType.PREDEFINED, sensorConfig))
    CONDITION_ID = condition.getId()
  }

  def "History controller - Get all - OK"() {
    given:
    def condition = conditionRepository.findById(CONDITION_ID).get()
    def history1Id = historyRepository
            .save(TestDataFixture.createHistory(condition, AlarmStatus.SOLVED)).getId()
    def history2Id = historyRepository
            .save(TestDataFixture.createHistory(condition, AlarmStatus.SOLVED)).getId()

    when:
    def response
            = restClient.get("/alarm-histories",
            ApiResponse<List<AlarmHistoryResponse>>
    )

    then:
    response.statusCode.is2xxSuccessful()
    def listHistories = response.body.data as List<AlarmHistoryResponse>
    listHistories.size() == 2
    listHistories.every() { it["id"] as Long in Arrays.asList(history1Id, history2Id) }
  }
}
