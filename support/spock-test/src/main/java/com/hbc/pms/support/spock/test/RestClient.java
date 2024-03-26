package com.hbc.pms.support.spock.test;

import com.hbc.pms.support.auth.JwtService;
import com.hbc.pms.support.web.response.ApiResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class RestClient {
  private final TestRestTemplate restTemplate;
  private final JwtService jwtService;

  public RestClient(TestRestTemplate restTemplate, JwtService jwtService) {
    this.restTemplate = restTemplate;
    this.jwtService = jwtService;
  }

  public <T> ResponseEntity<T> get(String url, Class<T> responseType) {
    return restTemplate.getForEntity(url, responseType);
  }

  public <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType) {
    HttpEntity<String> entity = new HttpEntity<>(headers);
    return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
  }

  public <T> ResponseEntity<T> get(String url, UserDetails userDetails, Class<T> responseType) {
    return get(url, constructHeadersFromUser(userDetails), responseType);
  }

  public <T> ResponseEntity<T> post(String url, Object request, Class<T> responseType) {
    return restTemplate.postForEntity(url, request, responseType);
  }

  public <T> ResponseEntity<T> put(String url, Object request, Class<T> responseType) {
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
    return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, responseType);
  }

  public <T> ResponseEntity<T> post(
      String url, Object request, HttpHeaders headers, Class<T> responseType) {
    HttpEntity<Object> entity = new HttpEntity<>(request, headers);
    return restTemplate.postForEntity(url, entity, responseType);
  }


  public <T> ResponseEntity<T> post(
      String url, Object request, UserDetails userDetails, Class<T> responseType) {
    HttpEntity<Object> entity = new HttpEntity<>(request, constructHeadersFromUser(userDetails));
    return restTemplate.exchange(
        url,
        HttpMethod.POST,
        entity,
        ParameterizedTypeReference.forType(
            ResolvableType.forClassWithGenerics(ApiResponse.class, responseType).getType()));
  }

  public <T> ResponseEntity<T> put(
      String url, Object request, HttpHeaders headers, Class<T> responseType) {
    HttpEntity<Object> entity = new HttpEntity<>(request, headers);
    return restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
  }

  public <T> ResponseEntity<T> put(
      String url, Object request, UserDetails userDetails, Class<T> responseType) {
    return put(url, request, constructHeadersFromUser(userDetails), responseType);
  }

  public void delete(String url, HttpHeaders headers) {
    HttpEntity<String> entity = new HttpEntity<>(headers);
    restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
  }

  public void delete(String url, UserDetails userDetails) {
    delete(url, constructHeadersFromUser(userDetails));
  }

  public void delete(String url) {
    restTemplate.delete(url);
  }

  private HttpHeaders constructHeadersFromUser(UserDetails userDetails) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + jwtService.generateToken(userDetails));
    return headers;
  }
}
