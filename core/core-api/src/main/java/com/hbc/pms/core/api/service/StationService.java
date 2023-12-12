package com.hbc.pms.core.api.service;

import com.hbc.pms.core.enums.StationEnum;
import com.hbc.pms.core.model.StationGeneralStateDto;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import org.springframework.stereotype.Service;


@Service
public class StationService {

  private final PlcService plcService;

  public StationService(PlcService plcService) {
    this.plcService = plcService;
  }

  // create a sample Enum in Java

  public StationGeneralStateDto getGeneralState(StationEnum station) {
    var stationPlcCoordinate = plcService.getPlcCoordinatesOfStations().get(station.getName());

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
