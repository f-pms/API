package com.hbc.pms.core.api.support.data;

import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.plc.api.IoResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.plc4x.java.api.value.PlcValue;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class DataProcessor {
  public Map<String, String> flattenPLCData(Map<String, IoResponse> rawData) {
    Map<String, String> flattenedData = new HashMap<>();

    for (Map.Entry<String, IoResponse> entry : rawData.entrySet()) {
      PlcValue plcValue = entry.getValue().getPlcValue();
      if (Objects.isNull(plcValue)) {
        continue;
      }
      flattenedData.put(entry.getKey(), plcValue.getObject().toString());
    }
    return flattenedData;
  }

  public Map<String, String> flattenToFigureMappedData(Map<String, IoResponse> rawData, Blueprint blueprint) {
    Map<String, String> flattenedData = new HashMap<>();
    var addressToFigureMap = blueprint.getAddressToSensorMap();

    for (Map.Entry<String, List<String>> addrToFigEntry :
      addressToFigureMap.entrySet()) {
      for (String figureId : addrToFigEntry.getValue()) {
        try {
          flattenedData.put(figureId, rawData.get(addrToFigEntry.getKey()).getPlcValue().getObject().toString());
        } catch (NullPointerException e) {
          log.debug("Address {} of figureID {} is not found in PLC", addrToFigEntry.getKey(), figureId);
        }
      }
    }

    return flattenedData;
  }

  public Map<String, Map<String, String>> process(Map<String, IoResponse> rawData, List<Blueprint> blueprintList) {
    Map<String, Map<String, String>> result = new HashMap<>();
    for (Blueprint blueprint : blueprintList) {
      Map<String, IoResponse> blueprintResponse = new HashMap<>();
      for (String address : blueprint.getAddresses()) {
        blueprintResponse.put(address, rawData.get(address));
      }
      result.put(blueprint.getName(), flattenToFigureMappedData(rawData, blueprint));
    }
    return result;
  }
}
