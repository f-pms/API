package com.hbc.pms.core.api.service;

import com.hbc.pms.core.model.StationGeneralStateDto;
import com.hbc.pms.core.model.enums.StationEnum;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import com.hbc.pms.plc.io.IoDetailsService;
import org.springframework.stereotype.Service;


@Service
public class StationService {

  private final PlcService plcService;
  private final IoDetailsService ioDetailsService;

  public StationService(PlcService plcService, IoDetailsService ioDetailsService) {
    this.plcService = plcService;
    this.ioDetailsService = ioDetailsService;
  }

  // create a sample Enum in Java

  public StationGeneralStateDto getGeneralState(StationEnum station) {
    var stationPlcCoordinate =
        ioDetailsService.getIoCoordinatesById(station.getName()).getPmsCoordinate();
    try {
      var isConnected = plcService.readBoolean(stationPlcCoordinate.getIsConnected());
      var temperature = plcService.readFloat(stationPlcCoordinate.getTemperature());
      var voltage = plcService.readInt(stationPlcCoordinate.getVoltage());
      return new StationGeneralStateDto(isConnected, temperature, voltage);
    } catch (S7Exception e) {
      throw new RuntimeException(e);
    }
  }
}
