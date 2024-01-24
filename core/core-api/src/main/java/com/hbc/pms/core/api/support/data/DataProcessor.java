package com.hbc.pms.core.api.support.data;

import com.hbc.pms.core.model.Blueprint;
import com.hbc.pms.plc.api.IoResponse;
import org.apache.plc4x.java.api.value.PlcValue;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
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

    public Map<String, Map<String, String>> process(Map<String, IoResponse> rawData, List<Blueprint> blueprintList) {
        Map<String, Map<String, String>> result = new HashMap<>();
        for (Blueprint blueprint : blueprintList) {
            Map<String, IoResponse> blueprintResponse = new HashMap<>();
            for (String address : blueprint.getAddresses()) {
                blueprintResponse.put(address, rawData.get(address));
            }
            result.put(blueprint.getName(), flattenPLCData(blueprintResponse));
        }
        return result;
    }
}
