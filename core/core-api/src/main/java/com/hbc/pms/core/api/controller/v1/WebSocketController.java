package com.hbc.pms.core.api.controller.v1;

import java.util.Map;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

  @SendTo("/topic/main")
  public Map<String, String> sendMainStationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/tr11")
  public Map<String, String> sendTR11StationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/tr12")
  public Map<String, String> sendTR12StationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/tr30")
  public Map<String, String> sendTR30StationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/tr31")
  public Map<String, String> sendTR31StationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/tr32")
  public Map<String, String> sendTR32StationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/tr33")
  public Map<String, String> sendTR33StationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/tr34")
  public Map<String, String> sendTR34StationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/tr42")
  public Map<String, String> sendTR42StationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/tr52")
  public Map<String, String> sendTR52StationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/tr72")
  public Map<String, String> sendTR72StationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/tr82")
  public Map<String, String> sendTR82StationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/all-meter")
  public Map<String, String> sendTRAllMeterStationData(Map<String, String> stationData) {
    return stationData;
  }

  @SendTo("/topic/alarm")
  public String sendAlarm(String alarm) {
    return alarm;
  }
}
