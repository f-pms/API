package com.hbc.pms.core.api.task;

import java.io.IOException;
import java.util.HashMap;

import com.hbc.pms.core.api.domain.Event;
import com.hbc.pms.core.api.domain.Message;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
public class SampleListener extends TextWebSocketHandler implements ApplicationListener<Event> {
  private HashMap<String, WebSocketSession> sessions = new HashMap<>();

  @Override
  public void onApplicationEvent(Event event) {
    var source = (Message) event.getSource();
//    System.out.println("Listener: ");
//    System.out.println("isConnected: " + source.isConnected());
//    System.out.println("temperature: " + source.temperature());
//    System.out.println("voltage: " + source.voltage());
    sessions.forEach((id, s) -> {
      try {
        s.sendMessage(new TextMessage("isConnected: " + source.isConnected()));
        s.sendMessage(new TextMessage("temperature: " + source.temperature()));
        s.sendMessage(new TextMessage("voltage: " + source.voltage()));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    sessions.put(session.getId(), session);
    super.afterConnectionEstablished(session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    sessions.remove(session.getId());
    super.afterConnectionClosed(session, status);
  }
}
