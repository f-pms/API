// package com.hbc.pms.core.integration;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Disabled;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.web.server.LocalServerPort;
// import org.springframework.messaging.converter.StringMessageConverter;
// import org.springframework.messaging.simp.stomp.StompFrameHandler;
// import org.springframework.messaging.simp.stomp.StompHeaders;
// import org.springframework.messaging.simp.stomp.StompSession;
// import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.web.socket.client.standard.StandardWebSocketClient;
// import org.springframework.web.socket.messaging.WebSocketStompClient;
// import org.springframework.web.socket.sockjs.client.SockJsClient;
// import org.springframework.web.socket.sockjs.client.WebSocketTransport;
//
// import java.lang.reflect.Type;
// import java.util.List;
// import java.util.concurrent.ArrayBlockingQueue;
// import java.util.concurrent.BlockingQueue;
//
// import static java.util.concurrent.TimeUnit.SECONDS;
// import static org.awaitility.Awaitility.await;
// import static org.junit.jupiter.api.Assertions.assertEquals;
//
// @SpringBootTest(
//    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
// )
// @ActiveProfiles("local")
// @Disabled
// public class WebsocketControllerTest {
//  @LocalServerPort
//  private Integer port;
//
//  private WebSocketStompClient webSocketStompClient;
//
//  @BeforeEach
//  void setup() {
//    this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(List.of(new
// WebSocketTransport(new StandardWebSocketClient()))));
//  }
//
//  private String getWsPath() {
//    return String.format("ws://localhost:%d/websocket", port);
//  }
//
//  @Test
//  void verifyGreetingIsReceived() throws Exception {
//    BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);
//    webSocketStompClient.setMessageConverter(new StringMessageConverter());
//    StompSession session = webSocketStompClient
//        .connectAsync(getWsPath(), new StompSessionHandlerAdapter() {})
//        .get(1, SECONDS);
//
//    session.subscribe("/topic/main", new StompFrameHandler() {
//      @Override
//      public Type getPayloadType(StompHeaders headers) {
//        return String.class;
//      }
//      @Override
//      public void handleFrame(StompHeaders headers, Object payload) {
//        blockingQueue.add((String) payload);
//      }
//    });
//
//    await()
//        .atMost(1, SECONDS)
//        .untilAsserted(() -> assertEquals("Hello, Mike!", blockingQueue.poll()));
//  }
// }
