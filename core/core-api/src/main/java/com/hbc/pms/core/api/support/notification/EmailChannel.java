package com.hbc.pms.core.api.support.notification;

import com.hbc.pms.core.model.AlarmCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailChannel implements Channel {
  private static final Pattern pattern = Pattern.compile("^email:(?<email>[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4})$");

  @Override
  public void notify(String method, AlarmCondition condition) {
    var matcher = pattern.matcher(method);
    var isMatch = matcher.find();
    if (!isMatch) {
      return;
    }
    var email = matcher.group("email");
    log.info(condition.getId() + " - Sending email to " + email);
  }
}
