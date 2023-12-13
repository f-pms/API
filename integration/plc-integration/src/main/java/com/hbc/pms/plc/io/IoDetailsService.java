package com.hbc.pms.plc.io;

public interface IoDetailsService {
  Type getType();

  IoCoordinates getIoCoordinatesById(String id);

  enum Type {
    STATIC,
    DYNAMIC
  }
}
