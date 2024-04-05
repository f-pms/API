package com.hbc.pms.core.api.service.alarm.notification;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.AlarmHistory;
import com.hbc.pms.core.model.enums.AlarmActionType;
import com.hbc.pms.core.model.enums.AlarmSeverity;
import io.vavr.control.Try;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PushNotificationChannel extends AbstractChannel {

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${hbc.push.url}")
  private String url;

  @Value("${hbc.push.auth}")
  private String auth;

  @Value("${hbc.push.title}")
  private String title;

  @Override
  protected boolean filter(AlarmAction action) {
    return AlarmActionType.PUSH_NOTIFICATION.equals(action.getType());
  }

  @Override
  protected void send(AlarmHistory history, AlarmCondition condition, AlarmAction action) {
    var headers = new HttpHeaders();
    headers.add("Authorization", "Basic " + auth);
    headers.setContentType(MediaType.APPLICATION_JSON);

    var body = new HashMap<String, Object>();
    body.put("title", title);
    body.put("topic", "alarm");
    body.put(
        "priority",
        Match(condition.getSeverity())
            .of(
                Case($(is(AlarmSeverity.URGENT)), 5),
                Case($(is(AlarmSeverity.HIGH)), 4),
                Case($(), 3)));
    body.put(
        "message",
        "Message: " + action.getMessage() + "; TriggeredAt: " + history.getTriggeredAt());

    var mapper = new ObjectMapper();
    var request =
        new HttpEntity<>(Try.of(() -> mapper.writeValueAsString(body)).getOrElse(""), headers);
    restTemplate.exchange(url, HttpMethod.POST, request, String.class);
  }
}
