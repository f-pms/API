package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.service.dto.StationGeneralStateDto;
import com.hbc.pms.plc.integration.mokka7.S7Client;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.integration.mokka7.type.AreaType;
import com.hbc.pms.plc.integration.mokka7.type.DataType;
import com.hbc.pms.plc.integration.mokka7.util.S7;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StationService {

  private final S7Client plcClient;

  @Value("${hbc.plc.url}")
  private String plcUrl;

  public StationService(S7Client plcClient) {
    this.plcClient = plcClient;
  }

  // TODO: add retries in case the PLC connection is interrupted
  @PostConstruct
  public void postConstruct() {
    try {
      plcClient.connect(plcUrl, 0, 1);
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
