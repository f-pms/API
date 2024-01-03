package com.hbc.pms.plc.io;

public class StaticIoBlueprintService implements IoBlueprintService {
  private final JsonIoBlueprintRepository jsonIoBlueprintRepository;

  public StaticIoBlueprintService(JsonIoBlueprintRepository jsonIoBlueprintRepository) {
    this.jsonIoBlueprintRepository = jsonIoBlueprintRepository;
  }

  @Override
  public Type getType() {
    return Type.STATIC;
  }

  @Override
  public Blueprint getBlueprintById(String id) {
    return jsonIoBlueprintRepository.get(id);
  }
}
