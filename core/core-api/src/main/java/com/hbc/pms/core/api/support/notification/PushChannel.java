package com.hbc.pms.core.api.support.notification;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.AlarmHistory;
import com.hbc.pms.core.model.enums.AlarmActionType;
import com.hbc.pms.core.model.enums.AlarmSeverity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PushChannel extends AbstractChannel {

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${hbc.push.url}")
  private String url;

  @Value("${hbc.push.auth}")
  private String auth;

  @Override
  protected boolean filter(AlarmAction action) {
    return AlarmActionType.PUSH.equals(action.getType());
  }

  @Override
  protected void send(AlarmHistory history, AlarmCondition condition, AlarmAction action) {
    var header = new HttpHeaders();
    header.add("Authorization", "Basic " + auth);
    header.add("Title", "RMS");
    header.add(
        "Priority",
        Match(condition.getSeverity())
            .of(
                Case($(is(AlarmSeverity.URGENT)), "5"),
                Case($(is(AlarmSeverity.HIGH)), "4"),
                Case($(), "3")));

    var body = "Message: " + action.getMessage() + "; TriggeredAt: " + history.getTriggeredAt();
    var request = new HttpEntity<>(body, header);
    restTemplate.exchange(url + "/alarm", HttpMethod.POST, request, String.class);
  }
}
