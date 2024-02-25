package com.hbc.pms.core.api.support.notification;

import com.hbc.pms.core.model.AlarmAction;
import com.hbc.pms.core.model.AlarmCondition;
import com.hbc.pms.core.model.enums.AlarmActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailChannel extends Channel {

  private final JavaMailSender emailSender;

  @Value("${hbc.mail.from}")
  private String from;

  @Value("${hbc.mail.subject}")
  private String subject;

  @Override
  protected boolean filter(AlarmAction action) {
    return AlarmActionType.EMAIL.equals(action.getType());
  }

  @Override
  protected void send(AlarmAction action, AlarmCondition condition) {
    var emails = action.getRecipients();
    var message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(emails.toArray(String[]::new));
    message.setSubject(subject);
    message.setText(condition.getId() + ": " + action.getMessage());
    emailSender.send(message);
  }
}
