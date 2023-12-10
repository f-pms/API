package com.hbc.core.api.service;

import com.hbc.core.api.service.dto.StationGeneralStateDto;
import com.hbc.support.plc.connector.S7Client;
import com.hbc.support.plc.connector.exception.S7Exception;
import com.hbc.support.plc.connector.type.AreaType;
import com.hbc.support.plc.connector.type.DataType;
import com.hbc.support.plc.connector.util.S7;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StationService {

  private final S7Client plcClient = new S7Client();
  private static final String PLC_URL = "hbc-plc.ohtgo.me";

  // TODO: add retries in case the PLC connection is interrupted
  @PostConstruct
  public void postConstruct() {
    try {
      plcClient.connect(PLC_URL, 0, 1);
    } catch (S7Exception e) {
      throw new RuntimeException(e);
    }
  }

  public StationGeneralStateDto getGeneralState() {
    try {
      final byte[] buffer = new byte[1024];
      var isConnected = plcClient.readBit(AreaType.DB, 1, 4, 0);
      plcClient.readArea(AreaType.DB, 1, 6, 1, DataType.REAL, buffer);
      var temperature = S7.getFloatAt(buffer, 0);
      var voltage = plcClient.readInt(AreaType.DB, 1, 10);
      return new StationGeneralStateDto(isConnected, temperature, voltage);
    } catch (S7Exception e) {
      throw new RuntimeException(e);
    }
  }
}
