package com.hbc.pms.plc.io;

public interface IoBlueprintService {
  Type getType();

  Blueprint getBlueprintById(String id);

  enum Type {
    STATIC,
    DYNAMIC
  }
}
