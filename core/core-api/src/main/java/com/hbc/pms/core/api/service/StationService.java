package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.service.dto.StationGeneralStateDto;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import org.springframework.stereotype.Service;

@Service
public class StationService {

  private final PlcService plcService;

  public StationService(PlcService plcService) {
    this.plcService = plcService;
  }

  public StationGeneralStateDto getGeneralState() {
    try {
      // TODO: move the dbNumber, startByte and Java Type to a JSON file
      var isConnected = plcService.readBoolean(1, 4);
      var temperature = plcService.readFloat(1, 6);
      var voltage = plcService.readInt(1, 10);
      return new StationGeneralStateDto(isConnected, temperature, voltage);
    } catch (S7Exception e) {
      throw new RuntimeException(e);
    }
  }
}
