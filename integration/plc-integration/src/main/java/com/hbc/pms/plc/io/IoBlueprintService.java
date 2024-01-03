package com.hbc.pms.plc.io;

import java.util.List;

public interface IoBlueprintService {
  Type getType();

  Blueprint getById(String id);

  List<Blueprint> getAll();
  enum Type {
    STATIC,
    DYNAMIC
  }
}
