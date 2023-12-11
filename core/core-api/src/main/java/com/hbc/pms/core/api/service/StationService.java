package com.hbc.pms.core.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.pms.core.api.constants.StationEnum;
import com.hbc.pms.core.api.service.dto.StationGeneralStateDto;
import com.hbc.pms.core.api.support.json.PlcCoordinates;
import com.hbc.pms.plc.integration.mokka7.exception.S7Exception;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class StationService {

  private final PlcService plcService;

  public StationService(PlcService plcService) {
    this.plcService = plcService;
  }

  // create a sample Enum in Java

  public StationGeneralStateDto getGeneralState(StationEnum name) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      PlcCoordinates plcCoordinates = mapper.readValue(readString, PlcCoordinates.class);
      System.out.println(plcCoordinates);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {

      var isConnected = plcService.readBoolean(1, 4);
      var temperature = plcService.readFloat(1, 6);
      var voltage = plcService.readInt(1, 10);
      return new StationGeneralStateDto(isConnected, temperature, voltage);
    } catch (S7Exception e) {
      throw new RuntimeException(e);
    }
  }
}
