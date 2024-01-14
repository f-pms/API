package com.hbc.pms.core.api.support.data;

import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.io.Blueprint;
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
            String responseAsString = "x";
            PlcValue plcValue = entry.getValue().getPlcValue();
            if (Objects.nonNull(plcValue)) {
                responseAsString = plcValue.getObject().toString();
            }
            flattenedData.put(entry.getKey(), responseAsString);
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
            result.put(blueprint.getId(), flattenPLCData(blueprintResponse));
        }
        return result;
    }
}
