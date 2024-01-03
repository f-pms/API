package com.hbc.pms.plc.io;

import java.util.List;

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
    public Blueprint getById(String id) {
        return jsonIoBlueprintRepository.get(id);
    }

    @Override
    public List<Blueprint> getAll() {
        return jsonIoBlueprintRepository.getAll();
    }
}
