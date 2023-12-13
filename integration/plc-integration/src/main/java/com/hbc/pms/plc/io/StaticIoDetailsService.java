package com.hbc.pms.plc.io;

public class StaticIoDetailsService implements IoDetailsService {
  private final JsonIoDetailsRepository jsonIoDetailsRepository;

  public StaticIoDetailsService(JsonIoDetailsRepository jsonIoDetailsRepository) {
    this.jsonIoDetailsRepository = jsonIoDetailsRepository;
  }

  @Override
  public Type getType() {
    return Type.STATIC;
  }

  @Override
  public IoCoordinates getIoCoordinatesById(String id) {
    return jsonIoDetailsRepository.get(id);
  }
}
