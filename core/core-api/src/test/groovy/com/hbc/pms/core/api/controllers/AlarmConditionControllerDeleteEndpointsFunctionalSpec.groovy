package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.request.UpdateAlarmConditionCommand
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.enums.AlarmSeverity
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.repository.AlarmActionRepository
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.AlarmHistoryRepository
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.support.spock.test.RestClient
import java.util.concurrent.ThreadLocalRandom
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.PendingFeature

class AlarmConditionControllerDeleteEndpointsFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  private RestClient restClient

  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  AlarmConditionRepository conditionRepository

  @Autowired
  AlarmActionRepository alarmActionRepository

  @Autowired
  AlarmHistoryRepository alarmHistoryRepository


  def setup() {
    def monitorBlueprint
            = blueprintRepository.findById(TestDataFixture.CUSTOM_ALARM_BLUEPRINT_ID).get()
    def sensorConfig
            = configurationRepository
            .save(
                    TestDataFixture.createSensorConfiguration(monitorBlueprint, TestDataFixture.PLC_ADDRESS_REAL_01)
            )
    def condition = conditionRepository.save(TestDataFixture.createDefaultConditionEntity(AlarmType.CUSTOM, sensorConfig))
    alarmHistoryRepository.save(TestDataFixture.createHistory(condition))
    alarmActionRepository.save(TestDataFixture.createAction(condition))
  }

  def "Delete alarm condition - OK and cascade deleted alarm histories"() {
    given:
    def condition = conditionRepository.findAll().first()
    def histories = condition.getHistories()

    when:
    restClient.delete("/alarm-conditions/${condition.id}")

    then:
    def deletedCondition = conditionRepository.findById(condition.id)
    def deletedHistories
            = alarmHistoryRepository.findAllById(
            histories.collect { it -> it.id }
    )
    deletedCondition.isEmpty()
    deletedHistories.size() == 0
  }

  def "Delete alarm condition - Not existing alarm condition - Bad request"() {
    given:
    def conditionCountBefore = conditionRepository.findAll().size()

    when:
    restClient.delete("/alarm-conditions/123")

    then:
    def conditionCountAfter = conditionRepository.findAll().size()
    conditionCountBefore == conditionCountAfter
  }

  def "Delete alarm action - OK"() {
    given:
    def condition = conditionRepository.findAll().first()
    def action = condition.getActions().get(0)

    when:
    restClient.delete("/alarm-conditions/${condition.id}/actions/${action.id}")

    then:
    def updatedCondition = conditionRepository.findById(condition.id)
    def deletedAction = alarmActionRepository.findById(action.id)
    updatedCondition.get().actions.size() == condition.actions.size() - 1
    deletedAction.isEmpty()
  }

  def "Delete alarm action - Not existing alarm condition - Bad request"() {
    given:
    def condition = conditionRepository.findAll().first()
    def actionCountBefore = condition.getActions().size()

    when:
    restClient.delete("/alarm-conditions/${condition.id}/actions/123")

    then:
    def actionCountAfter
            = conditionRepository.findById(condition.id).get().getActions().size()
    actionCountBefore == actionCountAfter
  }

  @PendingFeature
  def "Delete alarm action - Not existing alarm action - Bad request"() {
    given:
    def condition = conditionRepository.findAll().first()
    def action = condition.getActions().get(0)
    def actionCountBefore = condition.getActions().size()

    when:
    restClient.delete("/alarm-conditions/123/actions/${action.id}")

    then:
    def actionCountAfter
            = conditionRepository.findById(condition.id).get().getActions().size()
    actionCountBefore == actionCountAfter
  }
}
