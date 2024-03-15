package com.hbc.pms.core.api.services

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.WebSocketController
import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand
import com.hbc.pms.core.api.service.AlarmConditionService
import com.hbc.pms.core.api.service.AlarmPersistenceService
import com.hbc.pms.core.api.service.WebSocketService
import com.hbc.pms.core.api.support.notification.PopupChannel
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.core.model.enums.AlarmActionType
import com.hbc.pms.core.model.enums.AlarmSeverity
import com.hbc.pms.core.model.enums.AlarmStatus
import com.hbc.pms.core.model.enums.AlarmType
import com.hbc.pms.core.model.enums.BlueprintType
import com.hbc.pms.integration.db.entity.SensorConfigurationEntity
import com.hbc.pms.integration.db.repository.AlarmActionRepository
import com.hbc.pms.integration.db.repository.AlarmConditionRepository
import com.hbc.pms.integration.db.repository.AlarmHistoryRepository
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.plc.api.PlcConnector
import java.lang.reflect.Type
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.web.client.RestTemplate
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.Transport
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import spock.lang.Ignore
import spock.lang.Shared

@Ignore
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
  PlcConnector connector

  private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
  @Autowired
  WebSocketController webSocketController

  private static SockJsClient sockJsClient

  StompSession stompSession

  @Shared
  WebSocketStompClient webSocketStompClient = new WebSocketStompClient(new SockJsClient(
          List.of(new WebSocketTransport(new StandardWebSocketClient()))))

  Long CONDITION_ID = 1
  int ALLOWED_DELAY_SEC = 10

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

//    this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
//            List.of(new WebSocketTransport(new StandardWebSocketClient()))))
//
//    webSocketStompClient.setMessageConverter(new StringMessageConverter());

    List<Transport> transports = new ArrayList<>();
    transports.add(new WebSocketTransport(new StandardWebSocketClient()));
    RestTemplateXhrTransport xhrTransport = new RestTemplateXhrTransport(new RestTemplate());
    transports.add(xhrTransport);

    sockJsClient = new SockJsClient(transports);

//    webSocketStompClient.setMessageConverter(new StringMessageConverter())
    stompSession = webSocketStompClient
            .connectAsync(String.format("ws://localhost:%d/websocket", port), new StompSessionHandlerAdapter() {
            }).get(10, TimeUnit.SECONDS)
  }

  def "verify welcome message is sent"() {
    final CountDownLatch latch = new CountDownLatch(1);
    final AtomicReference<Throwable> failure = new AtomicReference<>();

    stompSession.subscribe("/topic/alarm", new StompFrameHandler() {
      @Override
      public Type getPayloadType(StompHeaders headers) {
        return byte[].class;
      }

      @Override
      void handleFrame(StompHeaders headers, Object payload) {
        String json = new String((byte[]) payload);
        stompSession.disconnect();
        latch.countDown();
      }
    })

    WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
    stompClient.connect("ws://localhost:{port}", headers, new StompSessionHandlerAdapter() {}, port);

    if (failure.get() != null) {
      throw new AssertionError("", failure.get());
    }

    if (!latch.await(5, TimeUnit.SECONDS)) {
      fail("Portfolio positions not received");
    }

    expect:
    latch.count == 0
  }

  def "Noti service - New alarm history with action Popup - Sent noti"() {
    when:
    def condition = conditionRepository.findById(CONDITION_ID).get()
    actionRepository.save(TestDataFixture.createPopupAction(condition))
    def history
            = historyRepository.save(TestDataFixture.createHistory(condition, AlarmStatus.TRIGGERED))
    plcValueTestFactory.setCurrentValue(TestDataFixture.PLC_ADDRESS_BOOL_01, true)
    connector.updateScheduler()
    def alarmData = ['key': 'value']
    webSocketController.sendAlarm(alarmData)

    then:
    Thread.sleep(ALLOWED_DELAY_SEC * 1000)
    def historyNew = historyRepository.findById(history.getId()).get()
    historyNew.status == AlarmStatus.SENT
    conditions.within(ALLOWED_DELAY_SEC * 1000, {
      1 * webSocketService.fireAlarm(_)
//      1 * popupChannel.send
    })
  }

  def "sendAlarm method is called once"() {
    given:
    SimpMessagingTemplate messagingTemplate = Mock()
    WebSocketController controller = new WebSocketController()

    when:
    Map<String, String> alarmData = ['key': 'value']
    controller.sendAlarm(alarmData)

    then:
    1 * messagingTemplate.convertAndSend("/topic/alarm", alarmData)
  }

  def "Noti service - No alarm history with action Popup - Not sent"() {

  }

  def "Noti service - New alarm history with action Email - Sent noti"() {

  }

  def "Noti service - No alarm history with action Email - Sent noti"() {}

  def "Noti service - New alarm history with 2 actions Popup and Email - Sent all noti"() {
  }

  void populateHistories(String address) {
    def target = address
    conditionRepository.deleteAll()
    historyRepository.deleteAll()
    def blueprint
            = blueprintRepository
            .findAllByTypeAndName(BlueprintType.ALARM, AlarmType.CUSTOM.toString())
            .first()
    def sensorConfig = SensorConfigurationEntity.builder()
            .address(target)
            .blueprint(blueprint)
            .build()
    configurationRepository.save(sensorConfig)

    //TODO: temporarily workaround, delete this 'connector' expression when updated the sensorConfigPersistenceService impl
    connector.updateScheduler()

    def conditionCommand = new CreateAlarmConditionCommand(sensorConfigurationId: sensorConfig.id,
            message: "High temperature detected",
            severity: AlarmSeverity.HIGH,
            type: AlarmType.CUSTOM,
            checkInterval: 30,
            timeDelay: 60,
            min: 20.0,
            max: 30.0,
            isEnabled: true,
            actions: [createDefaultAlarmActionCommand()])
    def condition = alarmConditionService.createAlarmCondition(conditionCommand)

    alarmPersistenceService.createHistoryByCondition(condition)
  }

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
}
