package com.hbc.pms.core.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailSenderConfig {
  @Bean
  public JavaMailSender javaMailSender() {
    var mailSender = new JavaMailSenderImpl();
    mailSender.setHost("mail.smtp2go.com");
    mailSender.setPort(2525);
    mailSender.setUsername("pms@ohtgo.me");
    mailSender.setPassword("P@ssw0rd000");

    var props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    return mailSender;
  }
}
