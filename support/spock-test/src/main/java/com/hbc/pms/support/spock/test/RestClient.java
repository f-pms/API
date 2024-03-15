package com.hbc.pms.support.spock.test;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RestClient {
  private final TestRestTemplate restTemplate;

  public RestClient(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public <T> ResponseEntity<T> get(String url, Class<T> responseType) {
    return restTemplate.getForEntity(url, responseType);
  }

  public <T> ResponseEntity<T> post(String url, Object request, Class<T> responseType) {
    return restTemplate.postForEntity(url, request, responseType);
  }

  public <T> ResponseEntity<T> put(String url, Object request, Class<T> responseType) {
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
    return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, responseType);
  }

  public void delete(String url) {
    restTemplate.delete(url);
  }

}
