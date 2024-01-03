package com.hbc.pms.plc.io;

import java.util.List;

public interface IoBlueprintRepository {
  void add(Blueprint blueprint);

  Blueprint get(String id);

  List<Blueprint> getAll();
}
