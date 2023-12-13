package com.hbc.pms.core.api.service;

import com.hbc.pms.core.model.enums.PlcTypeEnum;
import com.hbc.pms.plc.integration.mokka7.S7Client;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.integration.mokka7.type.AreaType;
import com.hbc.pms.plc.io.Coordinate;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlcService {

  private final S7Client plcClient = new S7Client();
  @Value("${hbc.plc.url}")
  private String plcUrl;

  @PostConstruct
  public void postConstruct() {
    // TODO: add retries in case the PLC connection is interrupted
    try {
      plcClient.connect(plcUrl, 0, 1);
    } catch (S7Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean readBoolean(Coordinate coordinate) throws S7Exception {
    validateType(coordinate, PlcTypeEnum.BOOL.getName());
    return plcClient.readBit(AreaType.DB, coordinate.getDb(), coordinate.getStartByte(), 0);
  }

  public int readInt(Coordinate coordinate) throws S7Exception {
    validateType(coordinate, PlcTypeEnum.DINT.getName());
    return plcClient.readInt(AreaType.DB, coordinate.getDb(), coordinate.getStartByte());
  }

  public float readFloat(Coordinate coordinate) throws S7Exception {
    validateType(coordinate, PlcTypeEnum.REAL.getName());
    return plcClient.readFloat(AreaType.DB, coordinate.getDb(), coordinate.getStartByte());
  }

  private void validateType(Coordinate coordinate, String type) {
    if (!Objects.equals(coordinate.getType(), type)) {
      throw new RuntimeException(String.format("Read value type is not expected to be a %s value", type));
    }
  }


}
