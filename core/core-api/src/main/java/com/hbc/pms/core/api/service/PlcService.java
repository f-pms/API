package com.hbc.pms.core.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.pms.core.api.constants.StationEnum;
import com.hbc.pms.core.api.support.json.Coordinate;
import com.hbc.pms.core.api.support.json.PlcCoordinates;
import com.hbc.pms.plc.integration.mokka7.S7Client;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.integration.mokka7.type.AreaType;
import com.hbc.pms.plc.integration.mokka7.type.DataType;
import com.hbc.pms.plc.integration.mokka7.util.S7;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class PlcService {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final S7Client plcClient = new S7Client();

  @Getter
  public final Map<String, PlcCoordinates> plcCoordinatesOfStations = new HashMap<>();

  @Value("${hbc.plc.url}")
  private String plcUrl;

  @PostConstruct
  public void postConstruct() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      PlcCoordinates plcCoordinates;
      for (StationEnum stationName : StationEnum.values()) {
        plcCoordinates = mapper.readValue(
            new ClassPathResource(String.format("plc-coordinate/%s.json", stationName.getName())).getInputStream(),
            PlcCoordinates.class
        );
        plcCoordinatesOfStations.put(stationName.getName(), plcCoordinates);
        log.info(mapper.writeValueAsString(plcCoordinates));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // TODO: add retries in case the PLC connection is interrupted
    try {
      plcClient.connect(plcUrl, 0, 1);
    } catch (S7Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean readBoolean(Coordinate coordinate) throws S7Exception {
    validateType(coordinate, "boolean");
    return plcClient.readBit(AreaType.DB, coordinate.getDb(), coordinate.getStartByte(), 0);
  }

  public int readInt(Coordinate coordinate) throws S7Exception {
    validateType(coordinate, "int");
    return plcClient.readInt(AreaType.DB, coordinate.getDb(), coordinate.getStartByte());
  }

  public float readFloat(Coordinate coordinate) throws S7Exception {
    validateType(coordinate, "float");
    final byte[] buffer = new byte[1024];
    plcClient.readArea(AreaType.DB, coordinate.getDb(), coordinate.getStartByte(), 1, DataType.REAL, buffer);
    return S7.getFloatAt(buffer, 0);
  }

  private void validateType(Coordinate coordinate, String type) {
    if (!Objects.equals(coordinate.getType(), type)) {
      throw new RuntimeException(String.format("Read value type is not expected to be a %s value", type));
    }
  }

}
