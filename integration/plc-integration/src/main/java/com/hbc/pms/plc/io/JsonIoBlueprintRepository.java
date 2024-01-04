package com.hbc.pms.plc.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.pms.core.model.enums.StationEnum;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JsonIoBlueprintRepository implements IoBlueprintRepository {
    public final Map<String, Blueprint> blueprintsMap = new LinkedHashMap<>();
    private final ObjectMapper objectMapper;

    public JsonIoBlueprintRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void add(Blueprint blueprint) {
        blueprintsMap.put(blueprint.getId(), blueprint);
    }

    @Override
    public Blueprint get(String id) {
        return blueprintsMap.getOrDefault(id, null);
    }

    @Override
    public List<Blueprint> getAll() {
        return blueprintsMap.values().stream().toList();
    }

    @PostConstruct
    public void init() throws IOException {
        Blueprint blueprint;
        for (StationEnum stationName : StationEnum.values()) {
            blueprint = objectMapper.readValue(
                new ClassPathResource(String.format("blueprints/%s.json", stationName.getName()))
                    .getInputStream(), Blueprint.class);
            blueprintsMap.put(blueprint.getId(), blueprint);
        }
    }
}
