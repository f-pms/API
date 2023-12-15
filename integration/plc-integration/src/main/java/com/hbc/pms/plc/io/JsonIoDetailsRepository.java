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
public class JsonIoDetailsRepository implements IoDetailsRepository {
  public final Map<String, IoCoordinates> ioCoordinatesMap = new LinkedHashMap<>();
  private final ObjectMapper objectMapper;

  public JsonIoDetailsRepository(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void add(IoCoordinates ioCoordinates) {
    ioCoordinatesMap.put(ioCoordinates.getId(), ioCoordinates);
  }

  @Override
  public IoCoordinates get(String id) {
    return ioCoordinatesMap.get(id);
  }

  @Override
  public List<IoCoordinates> getAll() {
    return ioCoordinatesMap.values().stream().toList();
  }

  @PostConstruct
  public void init() throws IOException {
    IoCoordinates ioCoordinates;
    for (StationEnum stationName : StationEnum.values()) {
      ioCoordinates = new IoCoordinates();
      ioCoordinates.setId(stationName.getName());
      ioCoordinates.setPmsCoordinate(
          objectMapper.readValue(
              new ClassPathResource(String.format("plc-coordinate/%s.json", stationName.getName()))
                  .getInputStream(),
              IoCoordinates.PmsCoordinate.class));
      ioCoordinatesMap.put(ioCoordinates.getId(), ioCoordinates);
      log.info(objectMapper.writeValueAsString(ioCoordinates));
    }
  }
}
