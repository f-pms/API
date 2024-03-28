package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.FunctionalTestSpec
import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.WebSocketController
import com.hbc.pms.core.api.service.WebSocketService
import com.hbc.pms.core.api.service.alarm.AlarmConditionService
import com.hbc.pms.core.api.service.alarm.AlarmPersistenceService
import com.hbc.pms.core.api.service.alarm.notification.PopupChannel
import com.hbc.pms.core.model.enums.AlarmStatus
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.integration.db.repository.*
import com.hbc.pms.plc.api.PlcConnector
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient

@Slf4j
class NotificationServiceIntegrationSpec extends FunctionalTestSpec {
  @Autowired
  AlarmPersistenceService alarmPersistenceService

  @Autowired
  AlarmConditionService alarmConditionService

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  AlarmConditionRepository conditionRepository

  @Autowired
  AlarmHistoryRepository historyRepository

  @Autowired
  SensorConfigurationRepository configurationRepository

  @Autowired
  AlarmActionRepository actionRepository

  @Autowired
  WebSocketService webSocketService

  @Autowired
  PopupChannel popupChannel

  @Autowired
  TestRestTemplate restTemplate

  @Autowired
  PlcConnector connector

  private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders()

  @Autowired
  WebSocketController webSocketController

  private static SockJsClient sockJsClient

  StompSession stompSession

  WebSocketStompClient webSocketStompClient

  @Autowired
  SimpMessagingTemplate template

  Long CONDITION_ID = 1
  int ALLOWED_DELAY_SEC = 8

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
    connector.updateScheduler()
    CONDITION_ID = condition.getId()
  }

  def "Noti service - New alarm history with action Popup - Sent noti"() {
    given:
    def condition = conditionRepository.findById(CONDITION_ID).get()
    condition.setEnabled(true)
    actionRepository.save(TestDataFixture.createPopupAction(condition))

    when:
    def history
            = historyRepository.save(TestDataFixture.createHistory(condition, AlarmStatus.TRIGGERED))
    plcValueTestFactory.setCurrentValue(TestDataFixture.PLC_ADDRESS_BOOL_01, true)

    then:
    conditions.within(ALLOWED_DELAY_SEC, {
      eventCollector.getEvent { r ->
        {
          r.payload.containsKey("message")
          r.payload.containsKey("triggeredAt")
        }
      }
    })
    def theHistory = historyRepository.findById(history.getId()).get()
    theHistory.status == AlarmStatus.SENT
  }

  def "Noti service - New alarm history with action Email - Sent noti"() {
    given:
    def condition = conditionRepository.findById(CONDITION_ID).get()
    condition.setEnabled(true)
    actionRepository.save(
            TestDataFixture.createEmailAction(
                    condition, new HashSet<String>(Arrays.asList("thinhle@gmail.com", "lowtothentotheg@kami.gam"))))

    when:
    def history
            = historyRepository.save(TestDataFixture.createHistory(condition, AlarmStatus.TRIGGERED))
    plcValueTestFactory.setCurrentValue(TestDataFixture.PLC_ADDRESS_BOOL_01, true)

    then:
    conditions.eventually {
      def theHistory = historyRepository.findById(history.getId()).get()
      theHistory.status == AlarmStatus.SENT
    }
    //TODO: check if email SENT
  }

  def "Noti service - New alarm history with action PushNoti - Sent noti"() {
    given:
    def condition = conditionRepository.findById(CONDITION_ID).get()
    condition.setEnabled(true)
    actionRepository.save(TestDataFixture.createPushNotiAction(condition))

    when:
    def history
            = historyRepository.save(TestDataFixture.createHistory(condition, AlarmStatus.TRIGGERED))
    plcValueTestFactory.setCurrentValue(TestDataFixture.PLC_ADDRESS_BOOL_01, true)

    then:
    conditions.eventually {
      def theHistory = historyRepository.findById(history.getId()).get()
      theHistory.status == AlarmStatus.SENT
    }
    //TODO: check if push notification SENT
  }

  def "Noti service - New alarm history with 3 actions Popup and Email - Sent all noti"() {
    given:
    def condition = conditionRepository.findById(CONDITION_ID).get()
    condition.setEnabled(true)
    actionRepository.save(TestDataFixture.createPushNotiAction(condition))
    actionRepository.save(TestDataFixture.createPopupAction(condition))
    actionRepository.save(TestDataFixture.createEmailAction(
            condition, new HashSet<String>(Arrays.asList("thinhle@gmail.com", "lowtothentotheg@kami.gam"))))

    when:
    def history
            = historyRepository.save(TestDataFixture.createHistory(condition, AlarmStatus.TRIGGERED))
    plcValueTestFactory.setCurrentValue(TestDataFixture.PLC_ADDRESS_BOOL_01, true)

    then:
    conditions.eventually {
      def theHistory = historyRepository.findById(history.getId()).get()
      theHistory.status == AlarmStatus.SENT
    }
    //TODO: check if all 3 notifications SENT
  }
}
